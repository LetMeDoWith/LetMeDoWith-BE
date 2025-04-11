package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskWithRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRegisterAvailChecker;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRegisterAvailChecker.RegisterAvailResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterDowithTaskService {
    
    private final DowithTaskRegisterAvailChecker dowithTaskRegisterAvailChecker;
    
    private final DowithTaskRepository dowithTaskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    
    /**
     * 두윗모드 Task 생성
     *
     * @param memberId
     * @param command
     */
    @Transactional
    public DowithTask createDowithTask(Long memberId, CreateDowithTaskCommand command) {
        
        Set<LocalDate> targetDateSet = command.getTargetDateSet();
        
        if (command.taskCategoryId() != null) {
            taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), memberId)
                                  .orElseThrow(() -> new RestApiException(
                                      FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST));
        }
        
        RegisterAvailResult registerAvailResult = dowithTaskRegisterAvailChecker.isRegisterAvail(
            targetDateSet, dowithTaskRepository.getDowithTasks(memberId, targetDateSet));
        
        if (!registerAvailResult.isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }
        
        DowithTask dowithTask = DowithTask.of(memberId, command.taskCategoryId(), command.title(),
                                              command.date(), command.startTime());
        
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
    public List<DowithTask> createDowithTaskWithRoutine(Long memberId,
                                                        CreateDowithTaskWithRoutineCommand command) {
        
        Set<LocalDate> targetDateSet = command.getTargetDateSet();
        
        RegisterAvailResult registerAvailResult = dowithTaskRegisterAvailChecker.isRegisterAvail(
            targetDateSet, dowithTaskRepository.getDowithTasks(memberId, targetDateSet));
        
        if (command.taskCategoryId() != null) {
            taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), memberId)
                                  .orElseThrow(() -> new RestApiException(
                                      FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST));
        }
        
        if (!registerAvailResult.isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }
        
        List<DowithTask> dowithTask = DowithTask.ofWithRoutine(memberId,
                                                               command.taskCategoryId(),
                                                               command.title(),
                                                               command.date(),
                                                               command.startTime(),
                                                               command.routineDates());
        
        return dowithTaskRepository.saveDowithTasks(dowithTask);
        
    }
    
    
}
