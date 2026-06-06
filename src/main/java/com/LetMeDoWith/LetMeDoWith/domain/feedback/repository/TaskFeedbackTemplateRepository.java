package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplate;
import java.util.Optional;

public interface TaskFeedbackTemplateRepository {
    Optional<TaskFeedbackTemplate> getTaskFeedbackTemplate(Long id);
}
