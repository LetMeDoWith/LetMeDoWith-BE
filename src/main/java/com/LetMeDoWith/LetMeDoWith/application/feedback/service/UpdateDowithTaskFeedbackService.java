package com.LetMeDoWith.LetMeDoWith.application.feedback.service;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDowithTaskFeedbackService {

    private final DowithTaskFeedbackRepository dowithTaskFeedbackRepository;

    /**
     * DowithTask에 대한 잔소리를 확인한다.
     *
     * @param dowithTaskFeedbackIds 확인할 DowithTaskFeedback의 ID 리스트
     * @param requestUserId 요청한 사용자 ID
     */
    @Transactional
    public void checkDowithFeedbacks(List<Long> dowithTaskFeedbackIds, String requestUserId) {
        dowithTaskFeedbackRepository
                .getFeedbacks(dowithTaskFeedbackIds, requestUserId)
                .forEach(DowithTaskFeedback::check);
    }
}
