package com.LetMeDoWith.LetMeDoWith.batch.scheduler;

import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import java.time.DayOfWeek;
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
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GodsaengSilcheonreoRankingJobScheduler {

    private final JobLauncher jobLauncher;

    @Qualifier("godsaengSilcheonreoRankingJob")
    private final Job godsaengSilcheonreoRankingJob;

    /**
     * 갓생실천러 랭킹 집계 배치 실행 트리거. 매주 월요일 02:00에 트리거하고, 마지막 월요일에만 잡을 실행한다.
     * <p>
     * TODO: 랭킹 오픈 후 스케줄러 주석 처리는 해제할 것
     */
    //    @Scheduled(cron = "0 0 2 * * MON")
    public void runGodsaengSilcheonreoRankingJob() {
        LocalDateTime executionDateTime = SystemTimeUtil.now();
        if (!DateTimeUtil.isLastDayOfWeekAt(executionDateTime, DayOfWeek.MONDAY, 2)) {
            log.info("Skip godsaengSilcheonreoRankingJob. executionDateTime={}", executionDateTime);
            return;
        }

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addLocalDateTime("executionDateTime", executionDateTime)
                .toJobParameters();
        try {
            JobExecution jobExecution = jobLauncher.run(godsaengSilcheonreoRankingJob, jobParameters);
            if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
                log.error(
                        "godsaengSilcheonreoRankingJob finished with non-completed status. status={}, executionDateTime={}",
                        jobExecution.getStatus(),
                        executionDateTime);
                throw new IllegalStateException(
                        "godsaengSilcheonreoRankingJob failed. status=" + jobExecution.getStatus());
            }
        } catch (Exception e) {
            log.error("Failed to run godsaengSilcheonreoRankingJob", e);
            throw new IllegalStateException("Failed to run godsaengSilcheonreoRankingJob", e);
        }
    }
}
