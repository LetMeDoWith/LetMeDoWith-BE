package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskContentsCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil.DateDifferences;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRegisterAvailChecker;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDowithTaskService {
    
    private final DowithTaskRegisterAvailChecker registerAvailChecker;
    
    private final DowithTaskRepository dowithTaskRepository;
    private final DowithTaskRoutineRepository dowithTaskRoutineRepository;
    
    private final TaskCategoryRepository taskCategoryRepository;
    
    /**
     * 두윗모드Task 내용 수정 및 루틴 생성
     *
     * @param memberId
     * @param command
     * @param routineDates
     */
    @Transactional
    public DowithTask updateContentsAndCreateRoutine(Long memberId,
                                                     UpdateDowithTaskContentsCommand command,
                                                     Set<LocalDate> routineDates) {
        
        DowithTask dowithTask = this.updateContents(memberId, command);
        if (!registerAvailChecker.isRegisterAvail(routineDates,
                                                  dowithTaskRepository.getDowithTasks(memberId,
                                                                                      routineDates))
                                 .isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }
        dowithTask.createRoutine(routineDates);
        
        return dowithTask;
        
    }
    
    /**
     * 두윗모드Task 내용 수정
     *
     * @param memberId
     * @param command
     */
    @Transactional
    public DowithTask updateContents(Long memberId,
                                     UpdateDowithTaskContentsCommand command) {
        
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(command.id(), memberId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        DOWITH_TASK_NOT_EXIST));
        
        TaskCategory taskCategory = taskCategoryRepository.getActiveTaskCategory(
                                                              command.taskCategoryId(), memberId)
                                                          .orElseThrow(() -> new RestApiException(
                                                              DOWITH_TASK_TASK_CATEGORY_NOT_EXIST));
        
        if (dowithTask.isRoutine()) {
            
            dowithTask.updateContentsWithRoutine(command.title(),
                                                 taskCategory.getId(),
                                                 command.date(),
                                                 command.startTime(),
                                                 dowithTaskRepository);
            
        } else {
            
            dowithTask.updateContents(command.title(),
                                      taskCategory.getId(),
                                      command.date(),
                                      command.startTime());
            
        }
        
        return dowithTask;
        
    }
    
    
    /**
     * 두윗모드Task 루틴 수정
     *
     * @param memberId
     * @param dowithTaskId
     * @param routineDates
     */
    @Transactional
    public DowithTask updateRoutine(Long memberId, Long dowithTaskId,
                                    Set<LocalDate> routineDates) {
        
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(dowithTaskId, memberId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        INVALID_REQUEST));
        
        if (!dowithTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        
        // input 중에서 업데이트 불가한 routine 일자(과거일자)가 기존 routine 중 업데이트 불가한 일자와 일치하는지 확인
        if (!dowithTask.getUpdateNotAvailRoutineDates().equals(routineDates.stream()
                                                                           .filter(date -> DateTimeUtil.isBefore(
                                                                               date,
                                                                               LocalDate.now()))
                                                                           .collect(
                                                                               Collectors.toSet()))) {
            throw new RestApiException(INVALID_REQUEST);
        }
        
        // input 중에서 업데이트 가능한 일자(현재,미래일자)와 기존 rountine 중 업데이트 가능한 일자 비교
        Set<LocalDate> existingUpdateAvailDates = dowithTask.getUpdateAvailRoutineDates();
        Set<LocalDate> toUpdateRoutineDates = routineDates.stream()
                                                          .filter(date -> DateTimeUtil.isAfterOrEqual(
                                                              dowithTask.getDate(),
                                                              date))
                                                          .collect(
                                                              Collectors.toSet());
        
        DateDifferences differences = DateTimeUtil.getDifferences(existingUpdateAvailDates,
                                                                  toUpdateRoutineDates);
        // 기존 routine 중 삭제해야할 routine 선별 및 삭제
        Set<LocalDate> toDeleteDates = differences.getLeftOnlyDates();
        dowithTask.deleteRoutine(toDeleteDates, dowithTaskRepository);
        
        // input 중에서 추가해야할 routine 선별 및 생성
        Set<LocalDate> toCreateDates = differences.getRightOnlyDates();
        
        if (!registerAvailChecker.isRegisterAvail(toCreateDates,
                                                  dowithTaskRepository.getDowithTasks(dowithTask.getMemberId(),
                                                                                      toCreateDates))
                                 .isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }
        
        dowithTask.addRoutine(toCreateDates, dowithTaskRepository);
        
        return dowithTask;
        
    }
    
}
