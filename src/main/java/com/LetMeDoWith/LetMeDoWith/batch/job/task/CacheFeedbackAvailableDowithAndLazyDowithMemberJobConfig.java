package com.LetMeDoWith.LetMeDoWith.batch.job.task;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.CacheFeedbackAvailableDowithAndLazyDowithMemberTasklet;
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
public class CacheFeedbackAvailableDowithAndLazyDowithMemberJobConfig {

    private final String JOB_NAME = "cacheFeedbackAvailableDowithAndLazyDowithMemberJob";
    private final String STEP_NAME = "cacheFeedbackAvailableDowithAndLazyDowithMemberStep";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job cacheFeedbackAvailableDowithAndLazyDowithMemberJob(
            Step cacheFeedbackAvailableDowithAndLazyDowithMemberStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(cacheFeedbackAvailableDowithAndLazyDowithMemberStep)
                .build();
    }

    @Bean
    @JobScope
    public Step cacheFeedbackAvailableDowithAndLazyDowithMemberStep(
            CacheFeedbackAvailableDowithAndLazyDowithMemberTasklet tasklet) {
        return new StepBuilder(STEP_NAME, jobRepository)
                .tasklet(tasklet, platformTransactionManager)
                .build();
    }
}
