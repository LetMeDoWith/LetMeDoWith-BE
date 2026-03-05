package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.TaskFeedbackTemplateQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;

import java.util.List;

public interface TaskFeedbackTemplateQueryRepository {

    List<TaskFeedbackTemplateQueryDto> getAllTaskFeedbackTemplates(CountryCode language);
}
