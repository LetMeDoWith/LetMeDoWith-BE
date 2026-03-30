package com.LetMeDoWith.LetMeDoWith.batch.scheduler;

import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
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
public class FeedbackKingJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job feedbackKingRankingJob;

    /**
     * 랭킹 집계 배치 실행 트리거.
     * 매주 월요일 02:00에 트리거하고, 마지막 월요일에만 잡을 실행한다.
     */
    @Scheduled(cron = "0 0 2 * * MON")
    public void feedbackKingRankingJob() {
        LocalDateTime executionDateTime = SystemTimeUtil.now();
        if (!DateTimeUtil.isLastDayOfWeekAt(executionDateTime, DayOfWeek.MONDAY, 2)) {
            log.info("Skip monthlyFeedbackKingRankingJob. executionDateTime={}", executionDateTime);
            return;
        }

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addLocalDateTime("executionDateTime", executionDateTime)
                .toJobParameters();
        try {
            jobLauncher.run(feedbackKingRankingJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to run monthlyFeedbackKingRankingJob", e);
        }
    }
}
