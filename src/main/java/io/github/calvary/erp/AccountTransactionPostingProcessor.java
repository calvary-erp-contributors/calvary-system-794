package io.github.calvary.erp;

import io.github.calvary.erp.errors.AccountCurrenciesDontMatchException;
import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import io.github.calvary.erp.errors.TransactionHasNotBeenProposedException;
import io.github.calvary.erp.errors.TransactionIsAlreadyDeletedException;
import io.github.calvary.service.AccountTransactionService;
import io.github.calvary.service.TransactionAccountService;
import io.github.calvary.service.TransactionCurrencyService;
import io.github.calvary.service.TransactionEntryQueryService;
import io.github.calvary.service.TransactionEntryService;
import io.github.calvary.service.criteria.TransactionEntryCriteria;
import io.github.calvary.service.dto.AccountTransactionDTO;
import io.github.calvary.service.dto.TransactionCurrencyDTO;
import io.github.calvary.service.dto.TransactionEntryDTO;

import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LongFilter;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AccountTransactionPostingProcessor implements PostingProcessorService<AccountTransactionDTO> {

    // private static final Logger log = LoggerFactory.getLogger(AccountTransactionPostingProcessor.class);


    private final TransactionAccountService transactionAccountService;
    private final TransactionCurrencyService transactionCurrencyService;
    private final TransactionEntryService transactionEntryService;
    private final TransactionEntryQueryService transactionEntryQueryService;
    private final AccountTransactionService accountTransactionService;
    private final PostingProcessorService<TransactionEntryDTO> transactionEntryPostingService;

    // private final NotificationMailService notificationMailService;
    // private final UserService userService;
    // private final UserRepository userRepository;

    private static final String ENTITY_NAME = "accountTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserNotificationService userNotificationService;

    public AccountTransactionPostingProcessor(
        TransactionAccountService transactionAccountService, 
        TransactionCurrencyService transactionCurrencyService, 
        TransactionEntryService transactionEntryService, 
        TransactionEntryQueryService transactionEntryQueryService, 
        AccountTransactionService accountTransactionService, 
        PostingProcessorService<TransactionEntryDTO> transactionEntryPostingService, 
        // NotificationMailService notificationMailService, 
        // UserService userService, 
        // UserRepository userRepository,
        UserNotificationService userNotificationService) {
        this.transactionAccountService = transactionAccountService;
        this.transactionCurrencyService = transactionCurrencyService;
        this.transactionEntryService = transactionEntryService;
        this.transactionEntryQueryService = transactionEntryQueryService;
        this.accountTransactionService = accountTransactionService;
        this.transactionEntryPostingService = transactionEntryPostingService;
        // this.notificationMailService = notificationMailService;
        // this.userService = userService;
        // this.userRepository = userRepository;
        this.userNotificationService = userNotificationService;
    }

    public AccountTransactionDTO post(AccountTransactionDTO accountTransaction) {

        if (!currenciesDoMatch(accountTransaction)) {
            throw new AccountCurrenciesDontMatchException("Account currency mismatch", ENTITY_NAME, "account.crn.mismatched");
        }

        if (!accountTransaction.getWasProposed()) {
            throw new TransactionHasNotBeenProposedException("Un-proposed posting", ENTITY_NAME, "un-proposed.transaction");
        }

        if (accountTransaction.getWasDeleted()) {
            throw new TransactionIsAlreadyDeletedException("Deleted posting attempted", ENTITY_NAME, "deleted.transaction");
        }

        if (accountTransaction.getWasPosted()) {

            return accountTransaction;
        }

        if (transactionBalanced(accountTransaction)) {
            transactionEntryQueryService.findByCriteria(getTransactionEntryCriteria(accountTransaction))
                .stream()
                .peek(transactionEntryPostingService::post)
                .peek(entry -> entry.setWasPosted(Boolean.TRUE))
                .forEach(transactionEntryService::save);
            accountTransaction.setWasPosted(Boolean.TRUE);
        }

        // notify user on posting
        userNotificationService.notifyUser(accountTransaction);

        return accountTransactionService.save(accountTransaction);
    }

    // private void notifyUser() {

    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    //     String username = "";
    //     Collection<? extends GrantedAuthority> authorities;

    //     if (authentication != null && authentication.isAuthenticated()) {

    //         Object principal = authentication.getPrincipal();

    //         if (principal instanceof UserDetails) {
    //             // Case 1: The principal is a UserDetails object
    //             UserDetails userDetails = (UserDetails) principal;
    //             username = userDetails.getUsername();
    //             // Other UserDetails-related operations...
    //         } else if (principal instanceof Jwt) {
    //             // Case 2: The principal is a Jwt object
    //             Jwt jwt = (Jwt) principal;
    //             System.out.println("JWT Claims: " + jwt.getClaims());
    //             jwt.getClaims().forEach((k,v) -> {

    //                 log.debug("JWT Claim: {} -> {}", k.strip(), v.toString());
    //             });
                
    //             username = jwt.getClaimAsString("sub");
    //         }
    //     } else {
    //         throw new UsernameNotFoundException("User with username: " + username);
    //     }

    //     String finalUsername = username;

    //     userRepository.findOneByLogin(username).ifPresentOrElse(
    //         (notificationMailService::sendMailNotification),
    //         () -> {throw new UsernameNotFoundException("username: " + finalUsername + " not found in the repo");}
    //     );
    // }

    private boolean currenciesDoMatch(AccountTransactionDTO accountTransaction) {

        return transactionEntryQueryService.findByCriteria(getTransactionEntryCriteria(accountTransaction))
            .stream()
            .map(entry -> entry.getTransactionAccount().getId())
            .map(transactionAccountService::findOne)
            .flatMap(Optional::stream)
            .map(account -> account.getTransactionCurrency().getId())
            .map(transactionCurrencyService::findOne)
            .flatMap(Optional::stream)
            .map(TransactionCurrencyDTO::getCode)
            .distinct()
            .count() <= 1;
    }

    private boolean transactionBalanced(AccountTransactionDTO accountTransaction) {

        AtomicBoolean state = new AtomicBoolean(false);

        transactionEntryQueryService.findByCriteria(getTransactionEntryCriteria(accountTransaction))
            .stream()
            .map(entry -> {
                if(entry.getTransactionEntryType() == TransactionEntryTypes.DEBIT) {
                    return entry.getEntryAmount();
                } else {
                    return entry.getEntryAmount().negate();
                }
            })
            .reduce(BigDecimal::add)
            .ifPresent(amount -> state.set(BigDecimal.ZERO.compareTo(amount) == 0));

        return state.get();
    }

    private TransactionEntryCriteria getTransactionEntryCriteria(AccountTransactionDTO accountTransaction) {
        // TODO Criteria
        TransactionEntryCriteria criteria = new TransactionEntryCriteria();
        LongFilter accountTransactionIdFilter = new LongFilter();

        accountTransactionIdFilter.setEquals(accountTransaction.getId());

        criteria.setAccountTransactionId(accountTransactionIdFilter);

        return criteria;
    }
}
