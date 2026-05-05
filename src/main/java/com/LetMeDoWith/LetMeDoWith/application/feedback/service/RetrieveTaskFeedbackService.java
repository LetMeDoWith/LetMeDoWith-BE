package com.LetMeDoWith.LetMeDoWith.application.feedback.service;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.*;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.TaskFeedbackTemplateQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.AggregateTaskFeedbacksQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.DowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.SentDowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.TaskFeedbackTemplateQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrieveTaskFeedbackService {

    private final DowithTaskFeedbackQueryRepository dowithTaskFeedbackQueryRepository;
    private final TaskFeedbackTemplateQueryRepository taskFeedbackTemplateQueryRepository;

    /**
     * DowithTask가 받은 잔소리 목록 조회
     *
     * @param dowithTaskId
     * @param pageable
     * @return
     */
    public RetrieveTaskFeedbackResult retrieveTaskReceivedFeedbacks(Long dowithTaskId, Pageable pageable) {

        Long totalCount = dowithTaskFeedbackQueryRepository.countFeedbacksByTaskId(dowithTaskId);
        List<DowithTaskFeedbackQueryDto> feedbackDtos = dowithTaskFeedbackQueryRepository.getFeedbacksByTaskId(
                dowithTaskId, pageable.getOffset(), pageable.getPageSize());
        List<TaskFeedbackTemplateQueryDto> feedbackTemplates =
                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(CountryCode.KR);
        return RetrieveTaskFeedbackResult.of(totalCount, feedbackDtos, feedbackTemplates);
    }

    /**
     * Dowith Task 잔소리 집계 조회
     * 잔소리 템플릿별로 몇 번씩 사용되었는지 조회합니다.
     *
     * @param dowithTaskId
     */
    public RetrieveTaskFeedbackAggregateResult retrieveTaskReceivedFeedbackAggregate(Long dowithTaskId) {
        List<AggregateTaskFeedbacksQueryDto> results = dowithTaskFeedbackQueryRepository.aggregateTaskFeedbacks(dowithTaskId);
        return RetrieveTaskFeedbackAggregateResult.from(results);
    }

    /**
     * 받은 잔소리 목록 조회
     *
     * @return
     */
    public RetrieveReceivedTaskFeedbackResult retrieveReceivedFeedbacks(Pageable pageable) {

        String memberId = AuthUtil.getMemberId();

        Long totalCount = dowithTaskFeedbackQueryRepository.countFeedbacksByReceiverId(memberId);
        List<DowithTaskFeedbackQueryDto> feedbackDtos = dowithTaskFeedbackQueryRepository.getFeedbacksByReceiverId(
                memberId, pageable.getOffset(), pageable.getPageSize());
        List<TaskFeedbackTemplateQueryDto> feedbackTemplates =
                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(CountryCode.KR);

        return RetrieveReceivedTaskFeedbackResult.of(totalCount, feedbackDtos, feedbackTemplates);
    }

    /**
     * 보낸 잔소리 목록 조회
     *
     * @return
     */
    public RetrieveSentTaskFeedbackResult retrieveSendFeedbacks(Pageable pageable) {

        String memberId = AuthUtil.getMemberId();

        Long totalCount = dowithTaskFeedbackQueryRepository.countFeedbacksBySenderId(memberId);
        List<SentDowithTaskFeedbackQueryDto> feedbackDtos = dowithTaskFeedbackQueryRepository.getFeedbacksBySenderId(
                memberId, pageable.getOffset(), pageable.getPageSize());
        List<TaskFeedbackTemplateQueryDto> feedbackTemplates =
                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(CountryCode.KR);

        return RetrieveSentTaskFeedbackResult.of(totalCount, feedbackDtos, feedbackTemplates);
    }

    /**
     * 잔소리 템플릿 조회
     *
     * @param countryCode
     * @return
     */
    public RetrieveTaskFeedbackTemplatesResult retrieveTaskFeedbackTemplates(CountryCode countryCode) {
        return RetrieveTaskFeedbackTemplatesResult.of(
                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(countryCode));
    }


}
