package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import java.util.List;

public interface TaskFeedbackTemplateQueryRepository {


    List<TaskFeedbackTemplateQueryDto> getAllTaskFeedbackTemplates(CountryCode language);
}