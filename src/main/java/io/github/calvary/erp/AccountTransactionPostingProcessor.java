package io.github.calvary.erp;

/*-
 * Copyright © 2023 - 2024 Calvary ERP Contributors (mailnjeru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.github.calvary.domain.enumeration.TransactionEntryTypes;
import io.github.calvary.erp.errors.AccountCurrenciesDontMatchException;
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
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LongFilter;

@Service
public class AccountTransactionPostingProcessor implements PostingProcessorService<AccountTransactionDTO> {

    private final TransactionAccountService transactionAccountService;
    private final TransactionCurrencyService transactionCurrencyService;
    private final TransactionEntryService transactionEntryService;
    private final TransactionEntryQueryService transactionEntryQueryService;
    private final AccountTransactionService accountTransactionService;
    private final PostingProcessorService<TransactionEntryDTO> transactionEntryPostingService;
    private final UserNotificationService userNotificationService;

    private static final String ENTITY_NAME = "accountTransaction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public AccountTransactionPostingProcessor(
        TransactionAccountService transactionAccountService,
        TransactionCurrencyService transactionCurrencyService,
        TransactionEntryService transactionEntryService,
        TransactionEntryQueryService transactionEntryQueryService,
        AccountTransactionService accountTransactionService,
        PostingProcessorService<TransactionEntryDTO> transactionEntryPostingService,
        UserNotificationService userNotificationService
    ) {
        this.transactionAccountService = transactionAccountService;
        this.transactionCurrencyService = transactionCurrencyService;
        this.transactionEntryService = transactionEntryService;
        this.transactionEntryQueryService = transactionEntryQueryService;
        this.accountTransactionService = accountTransactionService;
        this.transactionEntryPostingService = transactionEntryPostingService;
        this.userNotificationService = userNotificationService;
    }

    /**
     * Post account transaction
     *
     * @param accountTransaction
     * @return
     */
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
            transactionEntryQueryService
                .findByCriteria(getTransactionEntryCriteria(accountTransaction))
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

    /**
     * Check if currencies match
     *
     * @param accountTransaction
     * @return
     */
    private boolean currenciesDoMatch(AccountTransactionDTO accountTransaction) {
        return (
            transactionEntryQueryService
                .findByCriteria(getTransactionEntryCriteria(accountTransaction))
                .stream()
                .map(entry -> entry.getTransactionAccount().getId())
                .map(transactionAccountService::findOne)
                .flatMap(Optional::stream)
                .map(account -> account.getTransactionCurrency().getId())
                .map(transactionCurrencyService::findOne)
                .flatMap(Optional::stream)
                .map(TransactionCurrencyDTO::getCode)
                .distinct()
                .count() <=
            1
        );
    }

    /**
     * Check if transaction is balanced
     *
     * @param accountTransaction
     * @return
     */
    private boolean transactionBalanced(AccountTransactionDTO accountTransaction) {
        AtomicBoolean state = new AtomicBoolean(false);

        transactionEntryQueryService
            .findByCriteria(getTransactionEntryCriteria(accountTransaction))
            .stream()
            .map(entry -> {
                if (entry.getTransactionEntryType() == TransactionEntryTypes.DEBIT) {
                    return entry.getEntryAmount();
                } else {
                    return entry.getEntryAmount().negate();
                }
            })
            .reduce(BigDecimal::add)
            .ifPresent(amount -> state.set(BigDecimal.ZERO.compareTo(amount) == 0));

        return state.get();
    }

    /**
     * Extract transaction-entry-criteria
     *
     * @param accountTransaction
     * @return
     */
    private TransactionEntryCriteria getTransactionEntryCriteria(AccountTransactionDTO accountTransaction) {
        // TODO Criteria
        TransactionEntryCriteria criteria = new TransactionEntryCriteria();
        LongFilter accountTransactionIdFilter = new LongFilter();

        accountTransactionIdFilter.setEquals(accountTransaction.getId());

        criteria.setAccountTransactionId(accountTransactionIdFilter);

        return criteria;
    }
}
