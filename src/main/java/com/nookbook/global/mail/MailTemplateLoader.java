package com.nookbook.global.mail;


import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MailTemplateLoader {

    public String load(String fileName) {
        try {
            ClassPathResource resource =
                    new ClassPathResource("templates/mail/" + fileName);

            return new String(
                    resource.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email template: " + fileName, e);
        }
    }
}