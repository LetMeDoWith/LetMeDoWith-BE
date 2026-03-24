package com.LetMeDoWith.LetMeDoWith.batch.job.ranking;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.ranking.FeedbackKingTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@RequiredArgsConstructor
public class FeedbackKingRankingJobConfig {

    private static final String JOB_NAME = "feedbackKingRankingJob";
    private static final String STEP_NAME = "aggregateFeedbackKingRankingStep";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job feedbackKingRankingJob(Step aggregateFeedbackKingRankingStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(aggregateFeedbackKingRankingStep)
                .build();
    }

    @Bean
    @JobScope
    public Step aggregateFeedbackKingRankingStep(FeedbackKingTasklet tasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(tasklet, platformTransactionManager)
                .build();
    }
}
