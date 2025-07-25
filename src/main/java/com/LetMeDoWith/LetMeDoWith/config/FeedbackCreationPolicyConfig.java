package com.LetMeDoWith.LetMeDoWith.config;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.service.FeedbackCreationPolicy;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.service.TenMinutesFeedbackCreationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedbackCreationPolicyConfig {

    @Bean
    public FeedbackCreationPolicy feedbackCreationPolicy() {
        return new TenMinutesFeedbackCreationPolicy();
    }
}
