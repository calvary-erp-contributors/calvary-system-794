package io.github.calvary.erp;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import io.github.calvary.repository.UserRepository;
import io.github.calvary.service.PostingNotificationService;
import io.github.calvary.service.dto.AccountTransactionDTO;

/**
 * Email notification for transaction post sequence
 */
@Service
public class UserNotificationService {

    private static final Logger log = LoggerFactory.getLogger(UserNotificationService.class);

    private final PostingNotificationService postingNotificationService;
    public UserNotificationService(PostingNotificationService postingNotificationService,
            UserRepository userRepository) {
        this.postingNotificationService = postingNotificationService;
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;


    
    public void notifyUser(AccountTransactionDTO accountTransaction) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = "";
        Collection<? extends GrantedAuthority> authorities;

        if (authentication != null && authentication.isAuthenticated()) {

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                // Case 1: The principal is a UserDetails object
                UserDetails userDetails = (UserDetails) principal;
                username = userDetails.getUsername();
                // Other UserDetails-related operations...
            } else if (principal instanceof Jwt) {
                // Case 2: The principal is a Jwt object
                Jwt jwt = (Jwt) principal;
                System.out.println("JWT Claims: " + jwt.getClaims());
                jwt.getClaims().forEach((k,v) -> {

                    log.debug("JWT Claim: {} -> {}", k.strip(), v.toString());
                });
                
                username = jwt.getClaimAsString("sub");
            }
        } else {
            throw new UsernameNotFoundException("User with username: " + username);
        }

        String finalUsername = username;

        userRepository.findOneByLogin(username).ifPresentOrElse(
            (user -> postingNotificationService.sendMailNotification(user, accountTransaction)),
            () -> {throw new UsernameNotFoundException("username: " + finalUsername + " not found in the repo");}
        );
    }
}
