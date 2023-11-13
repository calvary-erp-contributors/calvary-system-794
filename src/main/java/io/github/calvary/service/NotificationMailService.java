package io.github.calvary.service;

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
