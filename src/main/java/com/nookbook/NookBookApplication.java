package com.nookbook;

import com.nookbook.global.config.YamlPropertySourceFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableBatchProcessing
@PropertySource(value = { "classpath:database/application-database.yml" }, factory = YamlPropertySourceFactory.class)
@PropertySource(value = { "classpath:webclient/application-webclient.yml" }, factory = YamlPropertySourceFactory.class)
@PropertySource(value = { "classpath:swagger/application-springdoc.yml" }, factory = YamlPropertySourceFactory.class)
@PropertySource(value = { "classpath:oauth2/application-oauth2.yml" }, factory = YamlPropertySourceFactory.class)
@PropertySource(value = { "classpath:s3/application-s3.yml" }, factory = YamlPropertySourceFactory.class)
@PropertySource(value = { "classpath:book/application-book.yml" }, factory = YamlPropertySourceFactory.class)
public class NookBookApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(NookBookApplication.class, args);
    }

}
