package com.nookbook.infrastructure.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CaffeineConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("bestSellers");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(25, TimeUnit.HOURS) // 갱신 주기(24시간) 보다 넉넉하게
                .maximumSize(100)
        );
        return cacheManager;
    }
}

