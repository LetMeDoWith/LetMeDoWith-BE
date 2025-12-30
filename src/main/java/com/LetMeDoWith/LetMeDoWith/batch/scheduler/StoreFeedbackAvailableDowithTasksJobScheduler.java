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
public class StoreFeedbackAvailableDowithTasksJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job storeFeedbackAvailableDowithTasksJob;

    /**
     * 레이지 두윗러 & 잔소리 대상 두윗 Redis 적재 배치. 매시 0분, 30분 마다 실행
     */
    @Scheduled(cron = "0 */30 * * * *")
    public void runStoreFeedbackAvailableDowithTasksJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addLocalDateTime("executionDateTime", SystemTimeUtil.now())
                .toJobParameters();

        try {
            jobLauncher.run(storeFeedbackAvailableDowithTasksJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace(); // TODO - Batch Exception 공통 처리
            log.error("Failed to run failDowithTaskJob", e);
        }
    }
}
