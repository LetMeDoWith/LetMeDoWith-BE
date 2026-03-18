package com.LetMeDoWith.LetMeDoWith.batch.scheduler;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
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
@Slf4j
public class RankingJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job jaksimSamilerRankingJob;
    private final Job godsaengSilcheonreoRankingJob;

    public RankingJobScheduler(
            JobLauncher jobLauncher,
            @Qualifier("jaksimSamilerRankingJob") Job jaksimSamilerRankingJob,
            @Qualifier("godsaengSilcheonreoRankingJob") Job godsaengSilcheonreoRankingJob) {
        this.jobLauncher = jobLauncher;
        this.jaksimSamilerRankingJob = jaksimSamilerRankingJob;
        this.godsaengSilcheonreoRankingJob = godsaengSilcheonreoRankingJob;
    }

    /**
     * 작심삼일러 랭킹 집계 배치 실행 트리거.
     * 매주 월요일 02:00에 트리거하고, 마지막 월요일에만 잡을 실행한다.
     */
    @Scheduled(cron = "0 0 2 * * MON")
    public void runJaksimSamilerRankingJob() {
        LocalDateTime executionDateTime = SystemTimeUtil.now();
        if (!isLastMondayAtTwo(executionDateTime)) {
            log.info("Skip jaksimSamilerRankingJob. executionDateTime={}", executionDateTime);
            return;
        }

        runJob(jaksimSamilerRankingJob, "jaksimSamilerRankingJob", executionDateTime);
    }

    /**
     * 갓생실천러 랭킹 집계 배치 실행 트리거.
     * 매주 월요일 02:00에 트리거하고, 마지막 월요일에만 잡을 실행한다.
     */
    @Scheduled(cron = "0 0 2 * * MON")
    public void runGodsaengSilcheonreoRankingJob() {
        LocalDateTime executionDateTime = SystemTimeUtil.now();
        if (!isLastMondayAtTwo(executionDateTime)) {
            log.info("Skip godsaengSilcheonreoRankingJob. executionDateTime={}", executionDateTime);
            return;
        }

        runJob(godsaengSilcheonreoRankingJob, "godsaengSilcheonreoRankingJob", executionDateTime);
    }

    private void runJob(Job job, String jobName, LocalDateTime executionDateTime) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addLocalDateTime("executionDateTime", executionDateTime)
                .toJobParameters();
        try {
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            if (jobExecution.getStatus() == BatchStatus.FAILED) {
                Throwable failureCause = jobExecution.getAllFailureExceptions().stream()
                        .findFirst()
                        .orElseGet(() -> new IllegalStateException("Batch execution failed without exception."));
                throw new IllegalStateException("Failed to run " + jobName, failureCause);
            }
        } catch (Exception e) {
            log.error("Failed to run {}", jobName, e);
            throw new IllegalStateException("Failed to run " + jobName, e);
        }
    }

    private boolean isLastMondayAtTwo(LocalDateTime targetDateTime) {
        if (targetDateTime.getDayOfWeek() != DayOfWeek.MONDAY) {
            return false;
        }
        if (targetDateTime.getHour() != 2) {
            return false;
        }
        return targetDateTime.toLocalDate().plusWeeks(1).getMonth() != targetDateTime.getMonth();
    }
}
