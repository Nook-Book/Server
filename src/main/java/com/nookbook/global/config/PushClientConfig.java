package com.nookbook.global.config;

import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PushClientConfig {

    @Bean
    public PushClient pushClient() {
        try {
            return new PushClient();
        } catch (PushClientException e) {
            log.error("PushClient 생성 실패", e);
            throw new IllegalStateException("Expo PushClient 초기화 중 오류 발생", e);
        }
    }
}