package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.TaskFeedbackTemplateRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.TaskFeedbackTemplateJpaRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class TaskFeedbackTemplateRepositoryImpl implements TaskFeedbackTemplateRepository {

    private final TaskFeedbackTemplateJpaRepository jpaRepository;

    @Override
    public Optional<TaskFeedbackTemplate> getTaskFeedbackTemplate(Long id) {
        return jpaRepository.findById(id);
    }
}
