package com.nookbook.domain.mail.application;

import com.nookbook.domain.verification.exception.EmailSendFailedException;
import com.nookbook.global.mail.MailTemplateBuilder;
import com.nookbook.global.util.MailHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final MailTemplateBuilder mailTemplateBuilder;

    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.mail.verification.subject}")
    private String subject;
    @Value("${spring.mail.verification.expire-minutes}")
    private int verificationExpireMinutes;

    public void sendPasswordResetVerificationEmail(String email, String code) {
        String content = mailTemplateBuilder.buildPasswordResetEmail(
                code,
                verificationExpireMinutes
        );

        sendMail(email, subject, content);
    }

    public void sendMail(String to, String subject, String content) {
        sendHtmlMail(to, subject, content);
    }

    private void sendHtmlMail(String to, String subject, String content) {
        try {
            MailHandler mailHandler = new MailHandler(mailSender);
            mailHandler.setTo(to);
            mailHandler.setSubject(subject);
            mailHandler.setText(content, true);
            mailHandler.setFrom(from);
            mailHandler.send();

        } catch (Exception ex) {
            log.error("Failed to send email. to={}, subject={}", to, subject, ex);
            throw new EmailSendFailedException();
        }
    }
}
