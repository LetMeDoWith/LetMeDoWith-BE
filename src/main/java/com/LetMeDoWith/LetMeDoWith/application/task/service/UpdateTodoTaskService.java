package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TodoTaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskRoutineConditionCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskRoutineContentCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineDateCalculator;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineSplitter;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineSplitter.TodoTaskRoutineSplitResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTodoTaskService {
    
    private final TodoTaskRepository todoTaskRepository;
    private final TodoTaskRoutineRepository todoTaskRoutineRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    
    private final TodoTaskRoutineDateCalculator routineDateCalculator;
    private final TodoTaskRoutineSplitter splitter;
    
    private final HolidayService holidayService;
    
    /**
     * 루틴이 아닌 TodoTask를 업데이트한다.
     * 컨텐츠를 업데이트 하거나, 루틴으로 변경할 수도 있다.
     *
     * @param memberId   TodoTask를 업데이트할 사용자의 ID
     * @param todoTaskId 업데이트할 TodoTask의 ID
     * @param command    업데이트할 정보 (카테고리 ID, 제목, 시작시간, 루틴정보)
     */
    @Transactional
    public void updateSingleTodoTask(String memberId,
                                     Long todoTaskId,
                                     UpdateTodoTaskCommand command) {
        
        TodoTask todoTask = todoTaskRepository
            .getTodoTask(todoTaskId, memberId)
            .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        
        TaskCategory category = taskCategoryRepository.
            getActiveTaskCategory(command.taskCategoryId(), memberId)
            .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        
        // 우선 컨텐츠를 업데이트
        todoTask.updateContent(command.title(),
                               category.getId(),
                               todoTask.getDate(),
                               command.startTime());
        
        if (command.routineCondition().isPresent()) {
            // 루틴 정보가 있다면 루틴 변환이므로 변환해주고 저장한다.
            TodoTaskRoutineCondition routineCondition = command.routineCondition()
                                                               .orElseThrow(() -> new RestApiException(
                                                                   FailResponseStatus.INVALID_REQUEST));
            
            // 루틴 반복 주기에 따른 루틴 수행일자 계산
            Set<LocalDate> routineDates =
                routineDateCalculator.computeRoutineDates(
                    routineCondition.cycle(),
                    routineCondition.startDate(),
                    routineCondition.endDate(),
                    routineCondition.pattern());
            
            if (Boolean.TRUE.equals(routineCondition.isExcludeHolidays())) {
                // 공휴일 목록 조회
                Set<LocalDate> holidays =
                    holidayService.getHolidays(CountryCode.KR,
                                               routineCondition.startDate(),
                                               routineCondition.endDate());
                
                routineDates.removeAll(holidays);
            }
            
            List<TodoTask> todoTasksWithRoutine =
                todoTask.createRoutine(routineDates,
                                       routineCondition.cycle(),
                                       routineCondition.pattern(),
                                       routineCondition.isExcludeHolidays());
            
            todoTaskRepository.saveTodoTasks(todoTasksWithRoutine);
        }
    }
    
    /**
     * 루틴 TodoTask의 컨텐츠만 업데이트한다.
     * todoTaskId에 해당하는 TodoTask만 업데이트 하거나, 루틴에 속한 모든 TodoTask에 적용 한다.
     *
     * @param memberId   TodoTask를 업데이트할 사용자의 ID
     * @param todoTaskId 업데이트할 TodoTask의 ID
     * @param command    업데이트할 정보 (카테고리 ID, 제목, 시작시간, 전체적용 여부)
     */
    @Transactional
    public void updateTodoTaskRoutineContent(String memberId,
                                             Long todoTaskId,
                                             UpdateTodoTaskRoutineContentCommand command) {
        TodoTask todoTask = todoTaskRepository
            .getTodoTask(todoTaskId, memberId)
            .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        
        if (!todoTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        
        List<TodoTask> todoTasksInRoutine = todoTaskRepository.getTodoTasks(todoTask.getRoutine());
        
        TaskCategory category = taskCategoryRepository
            .getActiveTaskCategory(command.taskCategoryId(), memberId)
            .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        
        if (command.isApplyToAll()) {
            // 모두 적용하는 경우, 입력받은 TodoTask를 기준으로 루틴을 분리한다.
            TodoTaskRoutineSplitResult splitResult = splitter.splitTodoTaskRoutine(
                todoTasksInRoutine,
                todoTask,
                todoTask.getRoutine());
            
            splitResult.getFutureTodoTasks()
                       .forEach(task -> {
                           task.updateContent(command.title(),
                                              category.getId(),
                                              task.getDate(),
                                              command.startTime());
                       });
        } else {
            // 루틴에 속한 TodoTask 중에서 todoTaskId에 해당하는 TodoTask만 업데이트 한다.
            todoTask.updateContent(command.title(),
                                   category.getId(),
                                   todoTask.getDate(),
                                   command.startTime());
            
            todoTask.detachRoutine();
        }
    }
    
    /**
     * 루틴 TodoTask의 루틴 정보를 업데이트 한다.
     *
     * @param memberId   TodoTask를 업데이트할 사용자의 ID
     * @param todoTaskId 업데이트할 TodoTask의 ID
     * @param command    업데이트할 루틴 정보 (시작/종료일자, 루틴 반복 주기, 루틴 반복 패턴, 루틴 제외 공휴일 여부)
     */
    @Transactional
    public void updateTodoTaskRoutineCondition(String memberId,
                                               Long todoTaskId,
                                               UpdateTodoTaskRoutineConditionCommand command) {
        
        TodoTask todoTask = todoTaskRepository
            .getTodoTask(todoTaskId, memberId)
            .orElseThrow(() -> new RestApiException(INVALID_REQUEST));
        
        if (!todoTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        
        List<TodoTask> todoTasksInRoutine = todoTaskRepository.getTodoTasks(todoTask.getRoutine());
        
        // 루틴 분리함
        TodoTaskRoutineSplitResult splitResult = splitter.splitTodoTaskRoutine(
            todoTasksInRoutine,
            todoTask,
            todoTask.getRoutine());
        
        // 기존의 미래 루틴과 날짜 전부 삭제
        todoTaskRepository.deleteTodoTasks(splitResult.getFutureTodoTasks());
        todoTaskRoutineRepository.delete(splitResult.getNewRoutine());
        
        // 입력받은 TodoTask를 시작으로 루틴 일자 다시 계산해서 루틴 재생성
        Set<LocalDate> routineDates =
            routineDateCalculator.computeRoutineDates(
                command.cycle(),
                command.startDate(),
                command.endDate(),
                command.pattern());
        
        if (Boolean.TRUE.equals(command.isExcludeHolidays())) {
            Set<LocalDate> holidays =
                holidayService.getHolidays(CountryCode.KR,
                                           command.startDate(),
                                           command.endDate());
            
            routineDates.removeAll(holidays);
        }
        
        List<TodoTask> todoTasks = TodoTask.ofWithRoutine(
            memberId,
            todoTask.getTaskCategoryId(),
            todoTask.getTitle(),
            todoTask.getDate(),
            todoTask.getStartTime(),
            routineDates,
            command.cycle(),
            command.pattern(),
            command.isExcludeHolidays()
        );
        
        todoTaskRepository.saveTodoTasks(todoTasks);
    }
    
}