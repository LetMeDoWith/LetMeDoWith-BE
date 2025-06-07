package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskSummary;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskSummaryService {

    private final TaskSummaryRepository taskSummaryRepository;

    @Transactional
    public int getRemainedDowithTaskCount(String memberId) {
        TaskSummary taskSummary = taskSummaryRepository.getTaskSummary(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        return taskSummary.getRemainedDowithTaskCount();
    }

    /**
     * 출석 체크 보상 지급
     *
     * @param memberId
     */
    @Transactional
    public void rewardAttendance(String memberId) {
        TaskSummary taskSummary = taskSummaryRepository.getTaskSummary(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        taskSummary.rewardAttendance();
    }

}
