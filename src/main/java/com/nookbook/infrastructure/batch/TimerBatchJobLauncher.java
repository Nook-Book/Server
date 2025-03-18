package com.nookbook.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class TimerBatchJobLauncher {
    private final JobLauncher jobLauncher;
    private final Job updateTimerJob;

    //@Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Scheduled(cron = "0 0/1 * * * ?") // 매분 0초에 실행
    public void runBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("timestamp", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();
            jobLauncher.run(updateTimerJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

