package com.LetMeDoWith.LetMeDoWith.application.feedback.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackRepository;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.service.FeedbackSendPolicy;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDowithTaskFeedbackService {

    private final DowithTaskFeedbackRepository dowithTaskFeedbackRepository;
    private final MemberRepository memberRepository;
    private final DowithTaskRepository dowithTaskRepository;
    private final FeedbackSendPolicy feedbackSendPolicy;

    /**
     * DowithTask에 대한 잔소리를 생성한다.
     *
     * @param senderId               잔소리를 보내는 사람의 ID
     * @param dowithTaskId           잔소리를 보낼 DowithTask의 ID
     * @param taskFeedbackTemplateId 잔소리 템플릿의 ID
     */
    @Transactional
    public void createDowithFeedback(String senderId, Long dowithTaskId, Long taskFeedbackTemplateId) {

        Member sender = memberRepository
                .getNormalStatusMember(senderId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        long feedbackCount = dowithTaskFeedbackRepository.countBySenderAndTask(dowithTaskId, senderId);
        Optional<DowithTaskFeedback> latestFeedback = dowithTaskFeedbackRepository.getLatest(dowithTaskId, senderId);

        if (!dowithTask.isFeedbackAvailable()) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        if (!feedbackSendPolicy.canSend(feedbackCount, latestFeedback, SystemTimeUtil.now())) {
            // TODO: 잔소리 재생성 제한에 걸린 경우 응답 코드 결정 후 적절한 FailResponseStatus로 교체
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        dowithTaskFeedbackRepository.save(
                DowithTaskFeedback.of(sender.getId(), dowithTask.getMemberId(), dowithTaskId, taskFeedbackTemplateId));
    }
}
