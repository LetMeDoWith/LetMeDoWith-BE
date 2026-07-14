package com.LetMeDoWith.LetMeDoWith.batch.job.task;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.task.NudgeTodoTaskStartNotifyTasklet;
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
public class NudgeTodoTaskStartJobConfig {

    private static final String JOB_NAME = "nudgeTodoTaskStartJob";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job nudgeTodoTaskStartJob(Step nudgeTodoTaskStartStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(nudgeTodoTaskStartStep)
                .build();
    }

    @Bean
    @JobScope
    public Step nudgeTodoTaskStartStep(NudgeTodoTaskStartNotifyTasklet nudgeTodoTaskStartNotifyTasklet) {
        return new StepBuilder("nudgeTodoTaskStartStep", jobRepository)
                .tasklet(nudgeTodoTaskStartNotifyTasklet, platformTransactionManager)
                .build();
    }
}
