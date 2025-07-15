package com.LetMeDoWith.LetMeDoWith.application.feedback.service;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackTemplatesResult;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.DowithTaskFeedbackQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.TaskFeedbackTemplateQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveTaskFeedbackService {

        private final DowithTaskFeedbackQueryRepository dowithTaskFeedbackQueryRepository;
        private final TaskFeedbackTemplateQueryRepository taskFeedbackTemplateQueryRepository;

        public RetrieveTaskFeedbackResult retrieveTaskFeedbacksByTaskId(Long taskId,
                        CountryCode language) {
                return RetrieveTaskFeedbackResult.of(
                                dowithTaskFeedbackQueryRepository.findAllByTaskId(taskId),
                                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(language));
        }

        public RetrieveTaskFeedbackResult retrieveTaskFeedbacksBySenderId(
                        String senderId, CountryCode language) {
                return RetrieveTaskFeedbackResult.of(
                                dowithTaskFeedbackQueryRepository.findAllBySenderId(senderId),
                                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(language));
        }

        public RetrieveTaskFeedbackResult retrieveTaskFeedbacksByReceiverId(
                        String receiverId, CountryCode language) {
                return RetrieveTaskFeedbackResult.of(
                                dowithTaskFeedbackQueryRepository.findAllByReceiverId(receiverId),
                                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(language));
        }

        public RetrieveTaskFeedbackTemplatesResult retrieveTaskFeedbackTemplates(CountryCode countryCode) {
                return RetrieveTaskFeedbackTemplatesResult
                                .of(taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(countryCode));
        }
}