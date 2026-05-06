package com.LetMeDoWith.LetMeDoWith.domain.feedback.service;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FeedbackCreationPolicy {

    private static final int FREE_THRESHOLD = 5;

    public boolean isAvailable(long feedbackCount, Optional<DowithTaskFeedback> latestFeedback, LocalDateTime now) {
        if (feedbackCount < FREE_THRESHOLD) {
            return true;
        }
        return latestFeedback
                .map(f -> f.getCreatedAt().plusMinutes(1).isBefore(now))
                .orElse(true);
    }
}
