package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.TodoTaskVO;
import com.LetMeDoWith.LetMeDoWith.application.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.application.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineDateComputeService;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineScheduleStrategy;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterTodoTaskService {
    
    private final TodoTaskRepository todoTaskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final TodoTaskRoutineDateComputeService dateComputeService;
    private final Map<String, TodoTaskRoutineScheduleStrategy> routineScheduleStrategies;
    
    /**
     * 루틴이 아닌 TodoTask를 생성한다.
     *
     * @param memberId TodoTask를 생성할 사용자의 ID
     * @param command  생성할 TodoTask의 정보 (카테고리 ID, 제목, 시작일, 시작시간, 루틴여부)
     * @return 생성된 TodoTask
     */
    public RegisterTodoTaskResult registerTodoTask(Long memberId, CreateTodoTaskCommand command) {
        if (command.taskCategoryId() != null) {
            taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), memberId)
                                  .orElseThrow(() -> new RestApiException(
                                      FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST));
        }
        
        TodoTask todoTask = TodoTask.of(memberId,
                                        command.taskCategoryId(),
                                        command.title(),
                                        command.startDate(),
                                        command.startTime());
        
        return RegisterTodoTaskResult.of(TodoTaskVO.from(todoTaskRepository.saveTodoTask(todoTask)));
    }
    
    /**
     * TodoTask 루틴을 생성한다.
     *
     * @param memberId TodoTask를 생성할 사용자의 ID
     * @param command  생성할 TodoTask의 정보 (카테고리 ID, 제목, 시작일, 종료일, 시작시간, 루틴여부, 루틴 반복 주기, 루틴 반복 패턴)
     * @return 생성된 루틴의 TodoTask 목록
     */
    public RegisterTodoTaskResult registerTodoTaskRoutine(Long memberId,
                                                          CreateTodoTaskCommand command) {
        if (command.taskCategoryId() != null) {
            taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), memberId)
                                  .orElseThrow(() -> new RestApiException(
                                      FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST));
        }
        
        String strategyKey = command.routineRepetitionCycle().name().toLowerCase()
            + "RoutineScheduleStrategy";
        TodoTaskRoutineScheduleStrategy strategy = routineScheduleStrategies.get(strategyKey);
        
        Set<LocalDate> routineDates = dateComputeService.computeRoutineDates(strategy,
                                                                             command.startDate(),
                                                                             command.endDate(),
                                                                             command.repetitionPattern());
        
        List<TodoTask> todoTasks = TodoTask.ofWithRoutine(memberId,
                                                          command.taskCategoryId(),
                                                          command.title(),
                                                          command.startDate(),
                                                          command.startTime(),
                                                          routineDates);
        
        return RegisterTodoTaskResult.of(todoTaskRepository.saveTodoTasks(todoTasks)
                                                           .stream()
                                                           .map(TodoTaskVO::from)
                                                           .toList(),
                                         routineDates);
    }
}