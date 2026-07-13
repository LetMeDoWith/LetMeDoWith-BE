package com.LetMeDoWith.LetMeDoWith.batch.job.task;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.task.NudgeDoriTaskStartNotifyTasklet;
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
public class NudgeDoriTaskStartJobConfig {

    private static final String JOB_NAME = "nudgeDoriTaskStartJob";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job nudgeDoriTaskStartJob(Step nudgeDoriTaskStartStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(nudgeDoriTaskStartStep)
                .build();
    }

    @Bean
    @JobScope
    public Step nudgeDoriTaskStartStep(NudgeDoriTaskStartNotifyTasklet nudgeDoriTaskStartNotifyTasklet) {
        return new StepBuilder("nudgeDoriTaskStartStep", jobRepository)
                .tasklet(nudgeDoriTaskStartNotifyTasklet, platformTransactionManager)
                .build();
    }
}
