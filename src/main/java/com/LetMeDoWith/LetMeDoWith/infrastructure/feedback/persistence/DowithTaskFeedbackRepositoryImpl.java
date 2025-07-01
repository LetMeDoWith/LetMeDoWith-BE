package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DowithTaskFeedbackRepositoryImpl implements DowithTaskFeedbackRepository {

    private final DowithTaskFeedbackJpaRepository repository;

    @Override
    public DowithTaskFeedback save(DowithTaskFeedback dowithTaskFeedback) {
        return repository.save(dowithTaskFeedback);
    }

    @Override
    public Optional<DowithTaskFeedback> getLatest(Long dowithTaskId, String senderId) {
        return repository.findTopByDowithTaskIdAndSenderIdOrderByCreatedAtDesc(dowithTaskId, senderId);
    }

    @Override
    public List<DowithTaskFeedback> getFeedbacks(List<Long> dowithTaskFeedbackIds) {
        return repository.findAllById(dowithTaskFeedbackIds);
    }
}
