package com.LetMeDoWith.LetMeDoWith.domain.feedback.service;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import java.time.LocalDateTime;
import java.util.Optional;

public interface FeedbackCreationPolicy {
    boolean isAdditionalFeedbackAvailable(Optional<DowithTaskFeedback> latestFeedback, LocalDateTime now);
}