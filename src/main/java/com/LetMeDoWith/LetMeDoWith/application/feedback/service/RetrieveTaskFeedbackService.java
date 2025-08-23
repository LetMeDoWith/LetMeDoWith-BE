package com.LetMeDoWith.LetMeDoWith.application.feedback.service;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackTemplatesResult;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.DowithTaskFeedbackQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.TaskFeedbackTemplateQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.DowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public RetrieveTaskFeedbackResult retrieveTaskFeedbacksByTaskId(Long dowithTaskId, Pageable pageable) {

        Long totalCount = dowithTaskFeedbackQueryRepository.countFeedbacksByTaskId(dowithTaskId);
        List<DowithTaskFeedbackQueryDto> feedbackDtos = dowithTaskFeedbackQueryRepository.getFeedbacksByTaskId(
                dowithTaskId, pageable.getOffset(), pageable.getPageSize());
        List<TaskFeedbackTemplateQueryDto> feedbackTemplates =
                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(CountryCode.KR);
        return RetrieveTaskFeedbackResult.of(totalCount, feedbackDtos, feedbackTemplates);
    }

    /**
     * 보낸 잔소리 목록 조회
     *
     * @return
     */
    public RetrieveTaskFeedbackResult retrieveSendFeedbacks(Pageable pageable) {

        String memberId = AuthUtil.getMemberId();
        CountryCode countryCode = CountryCode.KR;

        Long totalCount = dowithTaskFeedbackQueryRepository.countFeedbacksBySenderId(memberId);
        List<DowithTaskFeedbackQueryDto> feedbackDtos = dowithTaskFeedbackQueryRepository.getFeedbacksBySenderId(
                memberId, pageable.getOffset(), pageable.getPageSize());
        List<TaskFeedbackTemplateQueryDto> feedbackTemplates =
                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(countryCode);

        return RetrieveTaskFeedbackResult.of(totalCount, feedbackDtos, feedbackTemplates);
    }

    /**
     * 받은 잔소리 목록 조회
     *
     * @return
     */
    public RetrieveTaskFeedbackResult retrieveReceivedFeedbacks(Pageable pageable) {

        String memberId = AuthUtil.getMemberId();
        CountryCode countryCode = CountryCode.KR;

        Long totalCount = dowithTaskFeedbackQueryRepository.countFeedbacksByReceiverId(memberId);
        List<DowithTaskFeedbackQueryDto> feedbackDtos = dowithTaskFeedbackQueryRepository.getFeedbacksByReceiverId(
                memberId, pageable.getOffset(), pageable.getPageSize());
        List<TaskFeedbackTemplateQueryDto> feedbackTemplates =
                taskFeedbackTemplateQueryRepository.getAllTaskFeedbackTemplates(countryCode);

        return RetrieveTaskFeedbackResult.of(totalCount, feedbackDtos, feedbackTemplates);
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
