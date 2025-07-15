package com.LetMeDoWith.LetMeDoWith.domain.feedback.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import java.time.LocalDateTime;
import java.util.Optional;

@DomainService
public class TenMinutesFeedbackCreationPolicy implements FeedbackCreationPolicy {
    @Override
    public boolean canCreate(Optional<DowithTaskFeedback> latestFeedback, LocalDateTime now) {
        return latestFeedback
                .map(feedback -> feedback.getCreatedAt().plusMinutes(10).isBefore(now))
                .orElse(true);
    }
}