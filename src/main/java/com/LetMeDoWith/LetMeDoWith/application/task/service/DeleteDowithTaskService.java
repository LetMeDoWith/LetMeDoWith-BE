package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskSummary;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDowithTaskService {

    private final DowithTaskRepository dowithTaskRepository;
    private final DowithTaskRoutineRepository dowithTaskRoutineRepository;
    private final TaskSummaryRepository taskSummaryRepository;

    /**
     * 두윗모드 Task 삭제
     *
     * @param memberId
     * @param dowithTaskId
     */
    @Transactional
    public void delete(String memberId, Long dowithTaskId) {

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        TaskSummary taskSummary = taskSummaryRepository
                .getTaskSummary(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR));

        dowithTask.delete(dowithTaskRepository, dowithTaskRoutineRepository);
        taskSummary.plusRemainedDowithTaskCount(1);
    }

    /**
     * 두윗모드 Task + 루틴으로 등록된 모든 Task 삭제
     *
     * @param memberId
     * @param dowithTaskId
     */
    @Transactional
    public void deleteWithRoutines(String memberId, Long dowithTaskId) {

        DowithTask dowithTask = dowithTaskRepository
                .getDowithTask(dowithTaskId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        TaskSummary taskSummary = taskSummaryRepository
                .getTaskSummary(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR));

        int deletedDowithTaskCount = dowithTask.deleteWithRoutine(dowithTaskRepository, dowithTaskRoutineRepository);
        taskSummary.plusRemainedDowithTaskCount(deletedDowithTaskCount);
    }
}
