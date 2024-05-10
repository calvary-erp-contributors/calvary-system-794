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
import io.github.calvary.service.dto.AccountTransactionDTO;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

// import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Service for sending emails upon posting of transactions.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class PostingNotificationService {

    private static final String USER = "user";
    private static final String TRANSACTION_NUMBER = "transactionNumber";
    private static final String TRANSACTION_DATED = "transactionDated";

    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final Logger log = LoggerFactory.getLogger(NotificationMailService.class);

    private final MailService mailService;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public PostingNotificationService(
        JHipsterProperties jHipsterProperties,
        MailService mailService,
        SpringTemplateEngine templateEngine,
        MessageSource messageSource
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.mailService = mailService;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    @Async
    public void sendMailNotification(User user, AccountTransactionDTO accountTransaction) {
        log.debug("Sending notification email to '{}'", user.getEmail());

        String titleKey = "email.transaction.posting.notice.title";
        String templateName = "mail/contributionNoticeEmail";

        String langTag = user.getLangKey() == null ? "en" : user.getLangKey();
        String subject = messageSource.getMessage(titleKey, null, Locale.forLanguageTag(langTag));
        String content = contentProcessing(user, templateName, accountTransaction);

        mailService.sendEmail(user.getEmail(), subject, content, false, true);
    }

    private String contentProcessing(User user, String templateName, AccountTransactionDTO accountTransaction) {
        if (user.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getLogin());
            throw new EmailNotFoundException();
        }

        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(TRANSACTION_NUMBER, accountTransaction.getReferenceNumber());
        context.setVariable(TRANSACTION_DATED, accountTransaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());

        return templateEngine.process(templateName, context);
    }
}
