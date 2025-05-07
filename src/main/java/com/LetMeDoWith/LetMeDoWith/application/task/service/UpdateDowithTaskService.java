package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskContentsCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRegisterAvailChecker;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRoutineDateCalculator;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRoutineDateCalculator.RoutineDateResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDowithTaskService {
    
    private final DowithTaskRegisterAvailChecker registerAvailChecker;
    private final DowithTaskRoutineDateCalculator routineDateCalculator;
    
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
    public DowithTask updateContentsAndCreateRoutine(String memberId,
                                                     UpdateDowithTaskContentsCommand command,
                                                     Set<LocalDate> routineDates) {
        
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(command.id(), memberId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        INVALID_REQUEST));
        
        TaskCategory taskCategory = taskCategoryRepository.getActiveTaskCategory(
                                                              command.taskCategoryId(), memberId)
                                                          .orElseThrow(() -> new RestApiException(
                                                              INVALID_REQUEST));
        
        if (dowithTask.isContentsEditable()) {
            dowithTask.updateContents(command.title(),
                                      taskCategory.getId(),
                                      command.date(),
                                      command.startTime());
        } else {
            dowithTask.updateContents(command.title(), taskCategory.getId());
        }
        
        // 새롭게 생성할 일자 계산
        Set<LocalDate> toCreateDates = routineDates.stream()
                                                   .filter(date -> !date.isEqual(dowithTask.getDate()))
                                                   .collect(
                                                       Collectors.toSet());
        
        // 새 DowithTask 생성 가능 여부 validation
        if (!registerAvailChecker.isRegisterAvail(toCreateDates,
                                                  dowithTaskRepository.getDowithTasks(memberId,
                                                                                      toCreateDates))
                                 .isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }
        
        dowithTaskRepository.saveDowithTasks(dowithTask.createRoutine(routineDates));
        
        return dowithTask;
        
    }
    
    /**
     * 두윗모드Task 내용만 수정
     *
     * @param memberId
     * @param command
     */
    @Transactional
    public DowithTask updateContentsOnly(String memberId,
                                         UpdateDowithTaskContentsCommand command) {
        
        DowithTask dowithTask = dowithTaskRepository.getDowithTask(command.id(), memberId)
                                                    .orElseThrow(() -> new RestApiException(
                                                        INVALID_REQUEST));
        
        TaskCategory taskCategory = taskCategoryRepository.getActiveTaskCategory(
                                                              command.taskCategoryId(), memberId)
                                                          .orElseThrow(() -> new RestApiException(
                                                              INVALID_REQUEST));
        
        if (dowithTask.isRoutine()) {
            
            // TODO- 수정 대상이 어디까지인지 해당 정책 확인 필요 to 기획
            if (dowithTask.isContentsEditable()) {
                dowithTask.updateContentsWithRoutine(command.title(),
                                                     taskCategory.getId(),
                                                     command.date(),
                                                     command.startTime(),
                                                     dowithTaskRepository);
            } else {
                dowithTask.updateContentsWithRoutine(command.title(),
                                                     taskCategory.getId(),
                                                     dowithTaskRepository);
            }
            
            
        } else {
            // 루틴이 없는 DowithTask인 경우에만 TodoTask로 전환 가능
            // TODO - TodoTask Aggregate Merge 후 개발
            
            if (dowithTask.isContentsEditable()) {
                dowithTask.updateContents(command.title(),
                                          taskCategory.getId(),
                                          command.date(),
                                          command.startTime());
            } else {
                dowithTask.updateContents(command.title(), taskCategory.getId());
            }
            
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
    public DowithTask updateRoutine(String memberId, Long dowithTaskId,
                                    Set<LocalDate> routineDates) {
        
        final DowithTask dowithTask = dowithTaskRepository.getDowithTask(dowithTaskId, memberId)
                                                          .orElseThrow(() -> new RestApiException(
                                                              INVALID_REQUEST));
        
        LocalDateTime now = SystemTimeUtil.now();
        LocalDate nowDate = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();
        
        if (!dowithTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        
        // 수정 대상 루틴 날짜 계산
        // - 기존 루틴 일자에는 없고 새로운 루틴 일자에는 있어서 생성이 필요한 일자
        // - 기존 루틴 일자에는 있고 새로운 루틴 일자에는 없어서 삭제가 필요한 일자
        RoutineDateResult RoutineDatesToModifyResult = routineDateCalculator.getRoutineDatesToModify(
            dowithTask,
            routineDates);
        
        if (!RoutineDatesToModifyResult.isValid()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        
        // 새 루틴 등록으로, 삭제할 루틴 일자 + 연관 DowithTask 삭제
        dowithTask.deleteRoutine(RoutineDatesToModifyResult.getToDeleteRoutineDates(),
                                 dowithTaskRepository);
        
        // 새 루틴 등록으로, 새 루틴 생성 + 연관 DowithTask 생성
        Set<LocalDate> toCreateRoutineDates = RoutineDatesToModifyResult.getToCreateRoutineDates();
        if (!registerAvailChecker.isRegisterAvail(toCreateRoutineDates,
                                                  dowithTaskRepository.getDowithTasks(dowithTask.getMemberId(),
                                                                                      toCreateRoutineDates))
                                 .isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }
        
        dowithTask.addRoutine(toCreateRoutineDates, dowithTaskRepository);
        
        return dowithTask;
        
    }
    
}