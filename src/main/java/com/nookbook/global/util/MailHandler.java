package com.nookbook.global.util;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import com.nookbook.domain.verification.exception.EmailSendFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


@Slf4j
public class MailHandler {
    private JavaMailSender mailSender;
    private MimeMessage mimeMessage;
    private MimeMessageHelper mimeMessageHelper;


    public MailHandler(JavaMailSender mailSender) throws MessagingException {
        this.mailSender = mailSender;
        this.mimeMessage = mailSender.createMimeMessage();
        this.mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    }


    // Send mail
    public void send() {
        try {
            mailSender.send(mimeMessage);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new EmailSendFailedException();
        }
    }

    // Sender address
    public void setFrom(String fromAddress) throws MessagingException {
        mimeMessageHelper.setFrom(new InternetAddress(fromAddress));
    }

    // Recipient address
    public void setTo(String toAddress) throws MessagingException {
        mimeMessageHelper.setTo(toAddress);
    }

    // Recipient addresses
    public void setTo(String[] toAddresses) throws MessagingException {
        mimeMessageHelper.setTo(toAddresses);
    }

    // Subject
    public void setSubject(String subject) throws MessagingException {
        mimeMessageHelper.setSubject(subject);
    }

    // Body content
    public void setText(String text, boolean useHtml) throws MessagingException {
        mimeMessageHelper.setText(text, useHtml);
    }

    // Attachment
    public void setAttach(MultipartFile attachmentFile) throws IOException, MessagingException {
        mimeMessageHelper.addAttachment(Objects.requireNonNull(attachmentFile.getOriginalFilename()), attachmentFile);
    }

    // Inline image
    public void setInline(String contentId, String pathToInline) throws IOException, MessagingException {
        File file = new ClassPathResource(pathToInline).getFile();
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        mimeMessageHelper.addInline(contentId, fileSystemResource);
    }

}
