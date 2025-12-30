package com.LetMeDoWith.LetMeDoWith.batch.job.task;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.StoreFeedbackAvailableDowithTasksTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StoreFeedbackAvailableDowithTasksConfig {

    private final String JOB_NAME = "storeFeedbackAvailableDowithTasksJob";
    private final String STEP_NAME = "storeFeedbackAvailableDowithTasksStep";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job storeFeedbackAvailableDowithTasksJob(Step storeFeedbackAvailableDowithTasksStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(storeFeedbackAvailableDowithTasksStep)
                .build();
    }

    @Bean
    @JobScope
    public Step storeFeedbackAvailableDowithTasksStep(StoreFeedbackAvailableDowithTasksTasklet tasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(tasklet, platformTransactionManager)
                .build();
    }
}
