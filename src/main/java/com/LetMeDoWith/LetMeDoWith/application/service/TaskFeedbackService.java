package com.LetMeDoWith.LetMeDoWith.application.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackRepository;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskFeedbackService {
    
    private final DowithTaskFeedbackRepository dowithTaskFeedbackRepository;
    private final MemberRepository memberRepository;
    private final DowithTaskRepository dowithTaskRepository;
    
    /**
     * DowithTask에 대한 잔소리를 생성한다.
     *
     * @param senderId               잔소리를 보내는 사람의 ID
     * @param dowithTaskId           잔소리를 보낼 DowithTask의 ID
     * @param taskFeedbackTemplateId 잔소리 템플릿의 ID
     */
    @Transactional
    public void createDowithFeedback(String senderId,
                                     Long dowithTaskId,
                                     Long taskFeedbackTemplateId) {
        
        Member sender = memberRepository.getNormalStatusMember(senderId)
                                        .orElseThrow(() -> new RestApiException(FailResponseStatus.MEMBER_NOT_EXIST));
        
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(dowithTaskId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        FailResponseStatus.INVALID_REQUEST));
        
        Optional<DowithTaskFeedback> latestFeedback =
            dowithTaskFeedbackRepository.getLatest(dowithTaskId, senderId);
        
        if (dowithTask.isFeedbackAvailable()) {
            latestFeedback.ifPresent(feedback -> {
                if (feedback.isAdditionalFeedbackAvailable(sender.getId(), SystemTimeUtil.now())) {
                    dowithTaskFeedbackRepository.save(DowithTaskFeedback.of(sender.getId(),
                                                                            dowithTask.getMemberId(),
                                                                            dowithTaskId,
                                                                            taskFeedbackTemplateId));
                }
            });
        } else {
            // 피드백이 불가능한 상태에서 피드백을 시도하는 경우 예외 처리
            // 개별 메세지가 필요할 것으로 예상됨
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
    }
    
    /**
     * DowithTask에 대한 잔소리를 확인한다.
     *
     * @param dowithTaskFeedbackIds 확인할 DowithTaskFeedback의 ID 리스트
     */
    @Transactional
    public void checkDowithFeedback(List<Long> dowithTaskFeedbackIds) {
        dowithTaskFeedbackRepository.getFeedbacks(dowithTaskFeedbackIds)
                                    .forEach(DowithTaskFeedback::check);
        
    }
}