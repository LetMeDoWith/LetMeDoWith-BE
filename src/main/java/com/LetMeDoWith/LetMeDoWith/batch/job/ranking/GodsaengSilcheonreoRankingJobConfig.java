package com.LetMeDoWith.LetMeDoWith.batch.job.ranking;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.ranking.GodsaengSilcheonreoTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class GodsaengSilcheonreoRankingJobConfig {

    private static final String JOB_NAME = "godsaengSilcheonreoRankingJob";
    private static final String STEP_NAME = "aggregateGodsaengSilcheonreoRankingStep";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job godsaengSilcheonreoRankingJob(Step aggregateGodsaengSilcheonreoRankingStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(aggregateGodsaengSilcheonreoRankingStep)
                .build();
    }

    @Bean
    @JobScope
    public Step aggregateGodsaengSilcheonreoRankingStep(GodsaengSilcheonreoTasklet tasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(tasklet, platformTransactionManager)
                .build();
    }
}
