package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TaskRoutineDateCalculator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterTodoTaskService {

    private final TodoTaskRepository todoTaskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final TaskRoutineDateCalculator routineDateCalculator;
    private final HolidayService holidayService;

    /**
     * 루틴이 아닌 TodoTask를 생성한다.
     *
     * @param memberId TodoTask를 생성할 사용자의 ID
     * @param command  생성할 TodoTask의 정보 (카테고리 ID, 제목, 시작일, 시작시간, 루틴여부)
     * @return 생성된 TodoTask
     */
    public CreateTodoTaskResult registerTodoTask(String memberId, CreateTodoTaskCommand command) {
        if (command.taskCategoryId() != null) {
            taskCategoryRepository
                    .getActiveTaskCategory(command.taskCategoryId(), memberId)
                    .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        }

        TodoTask todoTask =
                TodoTask.of(memberId, command.taskCategoryId(), command.title(), command.date(), command.startTime());

        return CreateTodoTaskResult.of(todoTaskRepository.saveTodoTask(todoTask));
    }

    /**
     * TodoTask 루틴을 생성한다.
     *
     * @param memberId TodoTask를 생성할 사용자의 ID
     * @param command  생성할 TodoTask의 정보 (카테고리 ID, 제목, 시작일, 종료일, 시작시간, 루틴여부, 루틴 반복 주기, 루틴 반복 패턴)
     * @return 생성된 루틴의 TodoTask 목록
     */
    public CreateTodoTaskResult registerTodoTaskWithRoutine(String memberId, CreateTodoTaskCommand command) {

        if (command.taskCategoryId() != null) {
            taskCategoryRepository
                    .getTaskCategory(command.taskCategoryId(), Yn.TRUE)
                    .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        }

        List<TodoTask> todoTasks = new ArrayList<>();

        TaskRoutineCondition routineCondition = command.routineCondition();

        // 루틴 반복 주기에 따른 루틴 수행일자 계산
        Set<LocalDate> routineDates = routineDateCalculator.computeRoutineDates(
                routineCondition.cycle(),
                routineCondition.startDate(),
                routineCondition.endDate(),
                routineCondition.pattern());

        if (Boolean.TRUE.equals(routineCondition.isExcludeHolidays())) {
            // 공휴일 목록 조회
            Set<LocalDate> holidays = holidayService.getHolidays(
                    CountryCode.KR, routineCondition.startDate(), routineCondition.endDate());

            routineDates.removeAll(holidays);
        }

        TodoTask todoTask = TodoTask.ofWithRoutine(
                memberId,
                command.taskCategoryId(),
                command.title(),
                command.date(),
                command.startTime(),
                routineCondition.startDate(),
                routineCondition.endDate(),
                routineCondition.cycle(),
                routineCondition.pattern(),
                routineCondition.isExcludeHolidays());

        todoTasks.add(todoTask);
        todoTasks.addAll(TodoTask.of(todoTask, routineDates));

        return CreateTodoTaskResult.of(todoTaskRepository.saveTodoTasks(todoTasks), routineDates);
    }
}
