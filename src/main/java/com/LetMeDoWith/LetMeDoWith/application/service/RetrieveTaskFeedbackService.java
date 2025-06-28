package com.LetMeDoWith.LetMeDoWith.application.service;

import com.LetMeDoWith.LetMeDoWith.application.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.DowithTaskFeedbackQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.TaskFeedbackTemplateQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveTaskFeedbackService {
    
    private final DowithTaskFeedbackQueryRepository dowithTaskFeedbackQueryRepository;
    private final TaskFeedbackTemplateQueryRepository taskFeedbackTemplateQueryRepository;
    
    public RetrieveTaskFeedbackResult retrieveTaskFeedbacks(Long taskId, String language) {
        return RetrieveTaskFeedbackResult.of(
            dowithTaskFeedbackQueryRepository.findAllByTaskId(taskId),
            taskFeedbackTemplateQueryRepository.getTaskFeedbackTemplates(taskId, language)
        );
    }
    
}