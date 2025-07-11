package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import java.util.List;
import java.util.Optional;

public interface DowithTaskFeedbackRepository {

    DowithTaskFeedback save(DowithTaskFeedback dowithTaskFeedback);

    Optional<DowithTaskFeedback> getLatest(Long dowithTaskId, String senderId);

    List<DowithTaskFeedback> getFeedbacks(List<Long> dowithTaskFeedbackIds);

    List<DowithTaskFeedback> getFeedbacks(List<Long> dowithTaskIds, String memberId);
}