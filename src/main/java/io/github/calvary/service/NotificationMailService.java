package io.github.calvary.service;

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

import io.github.calvary.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class NotificationMailService {

    private final Logger log = LoggerFactory.getLogger(NotificationMailService.class);

    private final MailService mailService;

    public NotificationMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Async
    public void sendMailNotification(User user) {
        log.debug("Sending notification email to '{}'", user.getEmail());
        mailService.sendEmailFromTemplate(user, "mail/contributionNoticeEmail", "email.contribution.notice.title");
    }
}
