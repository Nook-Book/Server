package com.nookbook.domain.alarm.batch;


import com.nookbook.domain.alarm.domain.Alarm;
import com.nookbook.domain.alarm.domain.repository.AlarmRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AlarmCleanupJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final AlarmRepository alarmRepository;

    @Bean
    public Job alarmCleanupJob() {
        return new JobBuilder("alarmCleanupJob", jobRepository)
                .start(alarmCleanupStep())
                .build();
    }

    @Bean
    public Step alarmCleanupStep() {
        return new StepBuilder("alarmCleanupStep", jobRepository)
                .<Alarm, Alarm>chunk(100, transactionManager)
                .reader(alarmReader())
                .processor(alarmProcessor())
                .writer(alarmWriter())
                .build();
    }

    @Bean
    public ItemReader<Alarm> alarmReader() {
        return new JpaPagingItemReaderBuilder<Alarm>()
                .name("alarmReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM Alarm a WHERE a.createdAt < :threshold")
                .parameterValues(Map.of("threshold", LocalDateTime.now().minusDays(7)))
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<Alarm, Alarm> alarmProcessor() {
        return alarm -> alarm; // 그대로 전달 (삭제 대상)
    }

    @Bean
    public ItemWriter<Alarm> alarmWriter() {
        return alarmRepository::deleteAll;
    }
}