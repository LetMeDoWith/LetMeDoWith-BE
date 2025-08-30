package com.LetMeDoWith.LetMeDoWith.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SendDowithTaskStartNotificationJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;


    @Bean
    public Job buildJob() {
        return new JobBuilder("sendDowithTaskStartNotificationJob", jobRepository)
                .start()
    }

    @Bean
    public Step sendStep() {

    }
}
