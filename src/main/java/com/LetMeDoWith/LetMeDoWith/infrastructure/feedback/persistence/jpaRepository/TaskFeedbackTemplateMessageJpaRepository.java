package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplateMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskFeedbackTemplateMessageJpaRepository extends JpaRepository<TaskFeedbackTemplateMessage, Long> {}
