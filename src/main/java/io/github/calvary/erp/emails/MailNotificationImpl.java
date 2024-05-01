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

import io.github.calvary.erp.internal.InternalSalesReceiptEmailPersonaService;
import io.github.calvary.service.dto.DealerDTO;
import io.github.calvary.service.dto.SalesReceiptEmailPersonaDTO;
import io.github.calvary.service.dto.TransactionItemEntryDTO;
import io.github.calvary.service.dto.TransferItemEntryDTO;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

/**
 * Implements the notification for a single dealer with the sales receipt or bills details, this being
 * the transaction and transfer entries. We extract the email-persona of the dealer and apply that
 * for email details like the greeting, the address, sign-out signature and other banner details
 */
@Service
@Transactional
public class MailNotificationImpl implements MailNotification {

    private static final Logger log = LoggerFactory.getLogger(MailNotificationImpl.class);

    // Email variables
    private static final String RECIPIENT = "recipient";
    private static final String TRANSACTION_ITEMS = "transactionItems";
    private static final String TRANSFER_ITEMS = "transferItems";
    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    private final InternalSalesReceiptEmailPersonaService internalSalesReceiptEmailPersonaService;

    public MailNotificationImpl(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine,
        InternalSalesReceiptEmailPersonaService internalSalesReceiptEmailPersonaService
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.internalSalesReceiptEmailPersonaService = internalSalesReceiptEmailPersonaService;
    }

    @Override
    public void sendEmailSalesReceiptNotification(
        DealerDTO recipient,
        List<TransactionItemEntryDTO> transactionItems,
        List<TransferItemEntryDTO> transferItems
    ) {
        // TODO Implement mail-notification via email service

        List<SalesReceiptEmailPersonaDTO> personaList = new ArrayList<>();

        recipient
            .getSalesReceiptEmailPersonas()
            .forEach(persona -> {
                internalSalesReceiptEmailPersonaService.findOne(persona.getId()).ifPresent(personaList::add);
            });

        personaList.forEach(persona -> {
            sendEmailFromTemplate(
                persona,
                transactionItems,
                transferItems,
                "notification/receiptNotification",
                "receipt.notification.title"
            );
        });
    }

    @Async
    public void sendEmailFromTemplate(
        SalesReceiptEmailPersonaDTO persona,
        List<TransactionItemEntryDTO> transactionItems,
        List<TransferItemEntryDTO> transferItems,
        String templateName,
        String titleKey
    ) {
        if (persona.getMainEmail() == null) {
            log.debug("Email doesn't exist for persona name '{}'", persona.getPersonaName());
            return;
        }

        Locale locale = Locale.forLanguageTag(persona.getLanguageKeyCode());
        Context context = new Context(locale);

        context.setVariable(RECIPIENT, persona);
        context.setVariable(TRANSACTION_ITEMS, transactionItems);
        context.setVariable(TRANSFER_ITEMS, transferItems);

        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());

        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);

        sendEmail(persona.getMainEmail(), subject, content, false, true);
    }

    @Async
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
