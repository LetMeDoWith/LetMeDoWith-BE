package com.LetMeDoWith.LetMeDoWith.batch.scheduler;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyNotificationJobScheduler {

    private final JobLauncher jobLauncher;

    @Qualifier("dailyMorningNotificationJob")
    private final Job dailyMorningNotificationJob;

    @Qualifier("dailyEveningNotificationJob")
    private final Job dailyEveningNotificationJob;

    /**
     * 요일별 데일리 알림(아침) 배치 실행 트리거. 매일 09:00에 전체 NORMAL 회원 대상으로 발송한다.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void runDailyMorningNotificationJob() {
        LocalDateTime executionDateTime = SystemTimeUtil.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addLocalDateTime("executionDateTime", executionDateTime)
                .toJobParameters();
        try {
            JobExecution jobExecution = jobLauncher.run(dailyMorningNotificationJob, jobParameters);
            if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
                log.error(
                        "dailyMorningNotificationJob finished with non-completed status. status={}, executionDateTime={}",
                        jobExecution.getStatus(),
                        executionDateTime);
                throw new IllegalStateException(
                        "dailyMorningNotificationJob failed. status=" + jobExecution.getStatus());
            }
        } catch (Exception e) {
            log.error("Failed to run dailyMorningNotificationJob", e);
            throw new IllegalStateException("Failed to run dailyMorningNotificationJob", e);
        }
    }

    /**
     * 요일별 데일리 알림(저녁) 배치 실행 트리거. 매일 22:00에 전체 NORMAL 회원 대상으로 발송한다.
     */
    @Scheduled(cron = "0 0 22 * * *")
    public void runDailyEveningNotificationJob() {
        LocalDateTime executionDateTime = SystemTimeUtil.now();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addLocalDateTime("executionDateTime", executionDateTime)
                .toJobParameters();
        try {
            JobExecution jobExecution = jobLauncher.run(dailyEveningNotificationJob, jobParameters);
            if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
                log.error(
                        "dailyEveningNotificationJob finished with non-completed status. status={}, executionDateTime={}",
                        jobExecution.getStatus(),
                        executionDateTime);
                throw new IllegalStateException(
                        "dailyEveningNotificationJob failed. status=" + jobExecution.getStatus());
            }
        } catch (Exception e) {
            log.error("Failed to run dailyEveningNotificationJob", e);
            throw new IllegalStateException("Failed to run dailyEveningNotificationJob", e);
        }
    }
}
