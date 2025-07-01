package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskFeedbackTemplateJpaRepository
        extends JpaRepository<TaskFeedbackTemplate, Long> {}
