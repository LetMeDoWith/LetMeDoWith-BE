package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
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
}