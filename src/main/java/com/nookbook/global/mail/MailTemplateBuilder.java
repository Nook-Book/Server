package com.nookbook.global.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailTemplateBuilder {

    private final MailTemplateLoader mailTemplateLoader;

    public String buildPasswordResetEmail(String certNo, int minutes) {

        String fileName = "password_reset" + ".html";

        String template = mailTemplateLoader.load(fileName);

        return template
                .replace("{{CODE}}", certNo)
                .replace("{{MINUTES}}", String.valueOf(minutes));
    }
}