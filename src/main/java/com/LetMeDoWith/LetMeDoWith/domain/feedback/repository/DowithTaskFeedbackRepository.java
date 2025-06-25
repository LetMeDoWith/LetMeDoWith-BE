package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import java.util.Optional;

public interface DowithTaskFeedbackRepository {
    
    DowithTaskFeedback save(DowithTaskFeedback dowithTaskFeedback);
    
    Optional<DowithTaskFeedback> getLatest(Long dowithTaskId,
                                           String senderId);
}