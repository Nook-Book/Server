package com.nookbook;

import com.nookbook.global.config.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource(value = { "classpath:database/application-database.yml" }, factory = YamlPropertySourceFactory.class)
@PropertySource(value = { "classpath:webclient/application-webclient.yml" }, factory = YamlPropertySourceFactory.class)
@PropertySource(value = { "classpath:swagger/application-springdoc.yml" }, factory = YamlPropertySourceFactory.class)
@PropertySource(value = { "classpath:oauth2/application-oauth2.yml" }, factory = YamlPropertySourceFactory.class)
public class NookBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(NookBookApplication.class, args);
    }

}
