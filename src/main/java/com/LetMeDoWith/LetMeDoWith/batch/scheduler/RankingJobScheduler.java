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
public class RankingJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job jaksimSamilerRankingJob;

    /**
     * 랭킹 집계 배치 실행 트리거.
     * 매주 월요일 02:00에 트리거하고, 잡 내부에서 마지막 월요일 여부를 가드 처리한다.
     */
    @Scheduled(cron = "0 0 2 * * MON")
    public void runJaksimSamilerRankingJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addLocalDateTime("executionDateTime", SystemTimeUtil.now())
                .toJobParameters();
        try {
            jobLauncher.run(jaksimSamilerRankingJob, jobParameters);
        } catch (Exception e) {
            log.error("Failed to run jaksimSamilerRankingJob", e);
        }
    }
}
