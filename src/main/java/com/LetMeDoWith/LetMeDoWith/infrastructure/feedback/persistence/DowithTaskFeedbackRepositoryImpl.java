package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
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
        return repository.findTopByDowithTaskIdAndSenderIdOrderByCreatedAtDesc(dowithTaskId,
                                                                               senderId);
    }
}