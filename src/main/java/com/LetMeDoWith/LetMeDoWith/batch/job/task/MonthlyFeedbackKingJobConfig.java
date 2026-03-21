package com.LetMeDoWith.LetMeDoWith.batch.job.task;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.ranking.FeedbackKingTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MonthlyFeedbackKingJobConfig {

    private static final String JOB_NAME = "monthlyFeedbackKingJob";

    private final FeedbackKingTasklet feedbackKingTasklet;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job mothlyFeedbackKingJob(Step monthlyFeedbackKingStep) {
        return new org.springframework.batch.core.job.builder.JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(monthlyFeedbackKingStep)
                .build();
    }

    @Bean
    @JobScope
    public Step monthlyFeedbackKingStep(FeedbackKingTasklet feedbackKingTasklet) {
        return new StepBuilder("monthlyFeedbackKingStep", jobRepository)
                .tasklet(feedbackKingTasklet, platformTransactionManager)
                .build();
    }
}
