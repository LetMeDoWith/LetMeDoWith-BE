package com.LetMeDoWith.LetMeDoWith.application.feedback.service;

import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
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
     */
    @Transactional
    public void checkDowithFeedbacks(List<Long> dowithTaskFeedbackIds) {

        String memberId = AuthUtil.getMemberId();
        dowithTaskFeedbackRepository
                .getFeedbacks(dowithTaskFeedbackIds, memberId)
                .forEach(DowithTaskFeedback::check);
    }
}
