package com.nookbook.domain.timer.batch;

import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.timer.domain.repository.TimerRepository;
import com.nookbook.domain.user_book.domain.UserBook;
import com.nookbook.domain.user_book.domain.repository.UserBookRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
public class TimerBatchConfig {

    private final JobRepository jobRepository;
    private final TimerRepository timerRepository;
    private final UserBookRepository userBookRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job updateTimerJob() {
        return new JobBuilder("updateTimerJob", jobRepository)
                .start(updateTimerStep())
                .build();
    }

    @Bean
    public Step updateTimerStep() {
        return new StepBuilder("updateTimerStep", jobRepository)
                .<Timer, Timer>chunk(100, transactionManager)
                .reader(timerItemReader())
                .processor(timerItemProcessor())
                .writer(timerItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Timer> timerItemReader() {
        return new JpaPagingItemReaderBuilder<Timer>()
                .name("timerItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT t FROM Timer t WHERE t.isReading = true")
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemProcessor<Timer, Timer> timerItemProcessor() {
        return timer -> {
            BigInteger elapsedTime = BigInteger.valueOf(ChronoUnit.SECONDS.between(timer.getCreatedAt(), LocalDateTime.now()));
            timer.updateReadTime(elapsedTime);
            timer.updateIsReading(false);
            updateUserBookTotalReadTime(timer, elapsedTime);
            return timer;
        };
    }

    private void updateUserBookTotalReadTime(Timer timer, BigInteger elapsedTime) {
        UserBook userBook = timer.getUserBook();
        userBook.addTotalReadTime(elapsedTime);
        userBookRepository.save(userBook);
    }

    @Bean
    public ItemWriter<Timer> timerItemWriter() {
        return timers -> timerRepository.saveAll(timers);
    }
}
