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

import io.github.calvary.repository.UserRepository;
import io.github.calvary.service.PostingNotificationService;
import io.github.calvary.service.dto.AccountTransactionDTO;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tech.jhipster.config.JHipsterDefaults;

/**
 * Email notification for transaction post sequence
 */
@Service
public class UserNotificationService {

    private static final Logger log = LoggerFactory.getLogger(UserNotificationService.class);

    private final PostingNotificationService postingNotificationService;

    public UserNotificationService(PostingNotificationService postingNotificationService, UserRepository userRepository) {
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
            }
            //            else {
            //                // Case 2: The principal is a Jwt object
            //                OAuth2ResourceServerProperties.Jwt jwt = (OAuth2ResourceServerProperties.Jwt) principal;
            //                System.out.println("JWT Claims: " + jwt.getClaims());
            //                jwt.getClaims()
            //                    .forEach((k, v) -> {
            //                        log.debug("JWT Claim: {} -> {}", k.strip(), v.toString());
            //                    });
            //
            //                username = jwt.getClaimAsString("sub");
            //            }
        } else {
            throw new UsernameNotFoundException("User with username: " + username);
        }

        String finalUsername = username;

        userRepository
            .findOneByLogin(username)
            .ifPresentOrElse(
                (user -> postingNotificationService.sendMailNotification(user, accountTransaction)),
                () -> {
                    throw new UsernameNotFoundException("username: " + finalUsername + " not found in the repo");
                }
            );
    }
}
