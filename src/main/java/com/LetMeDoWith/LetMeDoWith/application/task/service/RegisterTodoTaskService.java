package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineDateCalculator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterTodoTaskService {
    
    private final TodoTaskRepository todoTaskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final TodoTaskRoutineDateCalculator routineDateCalculator;
    private final HolidayService holidayService;
    
    /**
     * 루틴이 아닌 TodoTask를 생성한다.
     *
     * @param memberId TodoTask를 생성할 사용자의 ID
     * @param command  생성할 TodoTask의 정보 (카테고리 ID, 제목, 시작일, 시작시간, 루틴여부)
     * @return 생성된 TodoTask
     */
    public RegisterTodoTaskResult createTodoTask(Long memberId, RegisterTodoTaskCommand command) {
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
        
        return RegisterTodoTaskResult.of(todoTaskRepository.saveTodoTask(todoTask));
    }
    
    /**
     * TodoTask 루틴을 생성한다.
     *
     * @param memberId TodoTask를 생성할 사용자의 ID
     * @param command  생성할 TodoTask의 정보 (카테고리 ID, 제목, 시작일, 종료일, 시작시간, 루틴여부, 루틴 반복
     *                 주기, 루틴 반복 패턴)
     * @return 생성된 루틴의 TodoTask 목록
     */
    public RegisterTodoTaskResult createTodoTaskWithRoutine(Long memberId,
                                                            RegisterTodoTaskCommand command) {
        
        if (command.taskCategoryId() != null) {
            taskCategoryRepository.getTaskCategory(command.taskCategoryId(), Yn.TRUE)
                                  .orElseThrow(() -> new RestApiException(
                                      FailResponseStatus.INVALID_REQUEST));
        }
        
        List<TodoTask> todoTasks;
        
        // 루틴 반복 주기에 따른 루틴 수행일자 계산
        Set<LocalDate> routineDates = routineDateCalculator.computeRoutineDates(
            command.routineCondition().cycle(),
            command.startDate(),
            command.endDate(),
            command.routineCondition().pattern());
        
        if (Boolean.TRUE.equals(command.routineCondition().isExcludeHolidays())) {
            // 공휴일 목록 조회
            Set<LocalDate> holidays = holidayService.getHolidays(CountryCode.KR,
                                                                 command.startDate(),
                                                                 command.endDate());
            
            // 공휴일 제외 메서드 사용
            todoTasks = TodoTask.ofWithRoutine(memberId,
                                               command.taskCategoryId(),
                                               command.title(),
                                               command.startDate(),
                                               command.startTime(),
                                               routineDates,
                                               holidays);
        } else {
            // 기본 메서드 사용
            todoTasks = TodoTask.ofWithRoutine(memberId,
                                               command.taskCategoryId(),
                                               command.title(),
                                               command.startDate(),
                                               command.startTime(),
                                               routineDates);
        }
        
        return RegisterTodoTaskResult.of(todoTaskRepository.saveTodoTasks(todoTasks),
                                         routineDates);
    }
}