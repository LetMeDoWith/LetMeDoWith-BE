package com.LetMeDoWith.LetMeDoWith.batch.job.notification.task;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.notification.task.FailDoriTaskNotifyTasklet;
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
public class DoriTaskFailNotifyJobConfig {

    private static final String JOB_NAME = "doriTaskFailNotifyJob";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job doriTaskFailNotifyJob(Step doriTaskFailNotifyStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(doriTaskFailNotifyStep)
                .build();
    }

    @Bean
    @JobScope
    public Step doriTaskFailNotifyStep(FailDoriTaskNotifyTasklet failDoriTaskNotifyTasklet) {
        return new StepBuilder("doriTaskFailNotifyStep", jobRepository)
                .tasklet(failDoriTaskNotifyTasklet, platformTransactionManager)
                .build();
    }
}
