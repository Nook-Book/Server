package com.nookbook.domain.alarm.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmCleanupJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job alarmCleanupJob;

    @Scheduled(cron = "0 0 3 * * *") // 매일 03시에 실행
    public void runAlarmCleanupJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 파라미터로 timestamp를 넘겨줌
                    .toJobParameters();
            jobLauncher.run(alarmCleanupJob, jobParameters);
            log.info("[스케줄링] 알림 정리 Job 실행 완료");
        } catch (Exception e) {
            log.error("알림 정리 Job 실행 실패", e);
        }
    }
}
