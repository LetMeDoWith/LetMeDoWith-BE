package com.LetMeDoWith.LetMeDoWith.batch.scheduler;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DowithTaskJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job failDowithTaskJob;

    /**
     * DowithTask 실패 처리 배치
     * 00분부터 5분 간격으로 실행
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void runFailTaskJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addLocalDateTime("executionDateTime", SystemTimeUtil.now())
                .toJobParameters();
        try {
            jobLauncher.run(failDowithTaskJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace(); // TODO - Batch Exception 공통 처리
            log.error("Failed to run failDowithTaskJob", e);
        }
    }
}
