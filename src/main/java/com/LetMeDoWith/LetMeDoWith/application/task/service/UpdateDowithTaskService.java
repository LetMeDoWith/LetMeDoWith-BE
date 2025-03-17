package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskContentsCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.DowithTaskRegisterAvailService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DowithTaskUpdater {
    
    private final DowithTaskRegisterAvailService registerAvailService;
    
    private final DowithTaskRepository dowithTaskRepository;
    private final DowithTaskRoutineRepository dowithTaskRoutineRepository;
    
    private final TaskCategoryRepository taskCategoryRepository;
    
    /**
     * 두윗모드Task 내용 수정 및 루틴 생성
     *
     * @param memberId
     * @param dowithTaskId
     * @param command
     * @param routineDates
     */
    @Transactional
    public DowithTask updateContentsAndCreateRoutine(Long memberId,
                                                     UpdateDowithTaskContentsCommand command,
                                                     Set<LocalDate> routineDates) {
        
        DowithTask dowithTask = updateContents(memberId, command);
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
            
            DowithTaskRoutine routine = dowithTask.getRoutine();
            List<DowithTask> dowithTasks = dowithTaskRepository.getDowithTasks(routine);
            Set<LocalDate> toUpdateDates = routine.getDatesAfterAndEqual(dowithTask.getDate());
            
            routine.updateRoutineDates(toUpdateDates);
            
            dowithTasks.forEach(task -> {
                if (toUpdateDates.contains(task.getDate())) {
                    task.updateContents(command.title(),
                                        taskCategory.getId(),
                                        command.date(),
                                        command.startTime());
                } else {
                    task.unlinkRoutine();
                }
            });
            
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
                                                        DOWITH_TASK_NOT_EXIST));
        
        if (!registerAvailService.isRegisterAvail(routineDates,
                                                  dowithTaskRepository.getDowithTasks(dowithTask.getMemberId(),
                                                                                      routineDates))
                                 .isAvail()) {
            throw new RestApiException(DOWITH_TASK_CREATE_COUNT_EXCEED);
        }
        
        if (dowithTask.isRoutine()) {
            
            // updateAvailDates 기준으로 업데이트 대상 판별
            Map<Boolean, List<DowithTask>> updateAvailTaskMap = getUpdateAvailTaskMap(dowithTask);
            
            // 기존 routine 삭제
            dowithTask.deleteRoutine(dowithTaskRoutineRepository, dowithTaskRepository);
            
            // 과거 Task 루틴 삭제
            updateAvailTaskMap.get(false)
                              .forEach(e -> e.deleteRoutine(dowithTaskRoutineRepository));
            
            // 현재, 미래 루틴 변경
            DowithTaskRoutine newRoutine = dowithTaskRoutineRepository.save(DowithTaskRoutine.from(
                updateAvailTaskMap.get(true)
                                  .stream()
                                  .map(DowithTask::getDate)
                                  .collect(Collectors.toSet())));
            updateAvailTaskMap.get(true).forEach(task -> task.updateRoutine(newRoutine));
            
        } else {
            
            dowithTaskRoutineRepository.delete(dowithTask.getRoutine());
            dowithTask.createRoutine(routineDates);
            
        }
        
        return dowithTask;
        
    }
    
    private Map<Boolean, List<DowithTask>> getUpdateAvailTaskMap(DowithTask dowithTask) {
        
        Set<LocalDate> updateAvailDates = dowithTask.getUpdateAvailRoutineDates();
        
        Map<Boolean, List<DowithTask>> updateAvailTaskMap = new HashMap<>();
        updateAvailTaskMap.put(true, new ArrayList<>());
        updateAvailTaskMap.put(false, new ArrayList<>());
        dowithTaskRepository.getDowithTasks(dowithTask.getRoutine())
                            .forEach(task ->
                                         updateAvailTaskMap.get(updateAvailDates.contains(task.getDate()))
                                                           .add(task)
                            );
        
        return updateAvailTaskMap;
    }
    
}
