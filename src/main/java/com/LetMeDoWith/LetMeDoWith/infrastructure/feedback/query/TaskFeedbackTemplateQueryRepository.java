package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import java.util.List;

public interface TaskFeedbackTemplateQueryRepository {
    
    List<TaskFeedbackTemplateQueryDto> getTaskFeedbackTemplates(Long id, String language);
    
    List<TaskFeedbackTemplateQueryDto> getAllTaskFeedbackTemplates(String language);
    
}