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
    
    public RetrieveTaskFeedbackResult retrieveTaskFeedbacksByTaskId(Long taskId, String language) {
        return RetrieveTaskFeedbackResult.of(
            dowithTaskFeedbackQueryRepository.findAllByTaskId(taskId),
            taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(language)
        );
    }
    
    public RetrieveTaskFeedbackResult retrieveTaskFeedbacksBySenderId(String senderId,
                                                                      String language) {
        return RetrieveTaskFeedbackResult.of(
            dowithTaskFeedbackQueryRepository.findAllBySenderId(senderId),
            taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(language)
        
        );
    }
    
    public RetrieveTaskFeedbackResult retrieveTaskFeedbacksByReceiverId(String receiverId,
                                                                        String language) {
        return RetrieveTaskFeedbackResult.of(
            dowithTaskFeedbackQueryRepository.findAllByReceiverId(receiverId),
            taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(language)
        );
    }
    
}