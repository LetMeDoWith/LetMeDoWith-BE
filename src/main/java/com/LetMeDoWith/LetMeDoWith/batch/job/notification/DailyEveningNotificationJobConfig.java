package com.LetMeDoWith.LetMeDoWith.batch.job.notification;

import com.LetMeDoWith.LetMeDoWith.batch.tasklet.notification.SendDailyEveningNotificationTasklet;
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
public class DailyEveningNotificationJobConfig {

    private static final String JOB_NAME = "dailyEveningNotificationJob";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job dailyEveningNotificationJob(Step dailyEveningNotificationStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dailyEveningNotificationStep)
                .build();
    }

    @Bean
    @JobScope
    public Step dailyEveningNotificationStep(SendDailyEveningNotificationTasklet sendDailyEveningNotificationTasklet) {
        return new StepBuilder("dailyEveningNotificationStep", jobRepository)
                .tasklet(sendDailyEveningNotificationTasklet, platformTransactionManager)
                .build();
    }
}
