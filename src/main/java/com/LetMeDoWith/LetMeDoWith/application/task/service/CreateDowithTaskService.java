package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskWithRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskSummary;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskSummaryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRegisterAvailChecker;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRegisterAvailChecker.RegisterAvailResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.*;

@Service
@RequiredArgsConstructor
public class CreateDowithTaskService {

    private final DowithTaskRegisterAvailChecker dowithTaskRegisterAvailChecker;

    private final DowithTaskRepository dowithTaskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final TaskSummaryRepository taskSummaryRepository;

    /**
     * 두윗모드 Task 생성
     *
     * @param memberId
     * @param command
     */
    @Transactional
    public DowithTask createDowithTask(String memberId, CreateDowithTaskCommand command) {

        Set<LocalDate> targetDateSet = command.getTargetDateSet();

        if (command.taskCategoryId() != null) {
            taskCategoryRepository
                    .getActiveTaskCategory(command.taskCategoryId(), memberId)
                    .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        }

        TaskSummary taskSummary = taskSummaryRepository.getTaskSummary(memberId)
                .orElseThrow(() -> new RestApiException(INTERNAL_SERVER_ERROR));

        RegisterAvailResult registerAvailResult =
                dowithTaskRegisterAvailChecker.isRegisterAvail(
                        targetDateSet, dowithTaskRepository.getDowithTasks(memberId, targetDateSet), taskSummary);

        if (!registerAvailResult.isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }

        DowithTask dowithTask =
                DowithTask.of(
                        memberId,
                        command.taskCategoryId(),
                        command.title(),
                        command.date(),
                        command.startTime());

        return dowithTaskRepository.saveDowithTask(dowithTask);
    }

    /**
     * 두윗모드 Task 생성 - 루틴이 있는 경우
     *
     * @param memberId
     * @param command
     * @return
     */
    @Transactional
    public List<DowithTask> createDowithTaskWithRoutine(
            String memberId, CreateDowithTaskWithRoutineCommand command) {

        Set<LocalDate> targetDateSet = command.getTargetDateSet();

        TaskSummary taskSummary = taskSummaryRepository.getTaskSummary(memberId)
                .orElseThrow(() -> new RestApiException(INTERNAL_SERVER_ERROR));

        RegisterAvailResult registerAvailResult =
                dowithTaskRegisterAvailChecker.isRegisterAvail(
                        targetDateSet, dowithTaskRepository.getDowithTasks(memberId, targetDateSet), taskSummary);

        if (command.taskCategoryId() != null) {
            taskCategoryRepository
                    .getActiveTaskCategory(command.taskCategoryId(), memberId)
                    .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        }

        if (!registerAvailResult.isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }

        List<DowithTask> dowithTask =
                DowithTask.ofWithRoutine(
                        memberId,
                        command.taskCategoryId(),
                        command.title(),
                        command.date(),
                        command.startTime(),
                        command.routineDates());

        return dowithTaskRepository.saveDowithTasks(dowithTask);
    }
}
