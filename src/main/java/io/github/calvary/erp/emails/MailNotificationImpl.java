package io.github.calvary.erp.emails;

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
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

@Service
public class MailNotificationImpl implements MailNotification {

    private static final Logger log = LoggerFactory.getLogger(MailNotificationImpl.class);

    // Email variables
    private static final String RECIPIENT = "recipient";
    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailNotificationImpl(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmailSalesReceiptNotification(
        DealerDTO recipient,
        List<TransactionItemEntryDTO> transactionItems,
        List<TransferItemEntryDTO> transferItems
    ) {}

    @Async
    public void sendEmailFromTemplate(
        DealerDTO recipient,
        List<TransactionItemEntryDTO> transactionItems,
        List<TransferItemEntryDTO> transferItems,
        String templateName,
        String titleKey
    ) {
        // TODO set receipt email on the dealer
        if (recipient.getMainEmail() == null) {
            log.debug("Email doesn't exist for recipient '{}'", recipient.getName());
            return;
        }
        // TODO set receipt lang-key on the dealer
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);

        // TODO create greetingTag on the dealer entity
        // TODO create preferredSignature on the dealer entity
        // TODO create preferredSignatureTag on the dealer entity
        context.setVariable(RECIPIENT, recipient);

        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());

        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);

        sendEmail(recipient.getMainEmail(), subject, content, false, true);
    }

    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content
        );

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to Recipient '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to Recipient '{}'", to, e);
        }
    }
}
