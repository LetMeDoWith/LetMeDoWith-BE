package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_PARAM_ERROR;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskWithRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TaskRoutineDateCalculator;
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

    private final TaskRoutineDateCalculator routineDateCalculator;
    private final TodoTaskRoutineSplitter splitter;

    private final HolidayService holidayService;

    /**
     * 한개의 TodoTask를 업데이트한다. 컨텐츠를 업데이트 하거나, 루틴으로 변경할 수도 있다.
     *
     * @param memberId   TodoTask를 업데이트할 사용자의 ID
     * @param todoTaskId 업데이트할 TodoTask의 ID
     * @param command    업데이트할 정보 (카테고리 ID, 제목, 시작시간, 루틴정보)
     */
    @Transactional
    public void updateSingleTodoTask(String memberId, Long todoTaskId, UpdateTodoTaskCommand command) {

        TodoTask todoTask = todoTaskRepository
                .getTodoTask(todoTaskId, memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        TaskCategory category = taskCategoryRepository
                .getActiveTaskCategory(command.taskCategoryId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        // 우선 컨텐츠를 업데이트
        todoTask.updateContent(command.title(), category.getId(), command.date(), command.startTime());

        // 루틴에 포함되는 TodoTask의 경우, 루틴을 분리한다.
        if (todoTask.isRoutine()) {
            todoTask.detachRoutine();
        }

        // 루틴이 아닌 태스크를 루틴으로 변경하는 경우
        if (command.routineCondition().isPresent()) {
            TaskRoutineCondition routineCondition = command.routineCondition().get();

            Set<LocalDate> holidays = routineCondition.isExcludeHolidays()
                    ? holidayService.getHolidayDates(
                            CountryCode.KR, routineCondition.startDate(), routineCondition.endDate())
                    : Set.of();

            // 루틴 반복 주기에 따른 루틴 수행일자 계산
            Set<LocalDate> routineDates = routineDateCalculator.computeRoutineDates(
                    routineCondition.cycle(),
                    routineCondition.startDate(),
                    routineCondition.endDate(),
                    routineCondition.pattern(),
                    holidays);

            todoTask.createRoutine(
                    routineCondition.startDate(),
                    routineCondition.endDate(),
                    routineCondition.cycle(),
                    routineCondition.pattern(),
                    routineCondition.isExcludeHolidays());

            todoTaskRepository.saveTodoTasks(TodoTask.of(todoTask, routineDates));
        }
    }

    /**
     * 루틴인 TodoTask를 업데이트한다. 루틴에 속한 모든 TodoTask에 적용 한다.
     *
     * @param command 업데이트할 정보 (카테고리 ID, 제목, 시작시간, 전체적용 여부)
     */
    @Transactional
    public void updateTodoTaskWithRoutine(UpdateTodoTaskWithRoutineCommand command) {

        String memberId = AuthUtil.getMemberId();
        TodoTask todoTask = todoTaskRepository
                .getTodoTask(command.todoTaskId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (!todoTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }

        List<TodoTask> todoTasksInRoutine = todoTaskRepository.getTodoTasks(todoTask.getRoutine());

        TaskCategory category = taskCategoryRepository
                .getActiveTaskCategory(command.taskCategoryId(), memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        // 입력받은 TodoTask를 기준으로 루틴을 분리한다.
        TodoTaskRoutineSplitResult splitResult =
                splitter.splitTodoTaskRoutine(todoTasksInRoutine, todoTask, todoTask.getRoutine());

        splitResult.getNewTodoTasks().forEach(task -> {
            task.updateContent(command.title(), category.getId(), task.getDate(), command.startTime());
        });
    }

    /**
     * 루틴 TodoTask의 루틴 정보를 업데이트 한다.
     *
     * @param memberId   TodoTask를 업데이트할 사용자의 ID
     * @param todoTaskId 업데이트할 TodoTask의 ID
     * @param command    업데이트할 루틴 정보 (시작/종료일자, 루틴 반복 주기, 루틴 반복 패턴, 루틴 제외 공휴일 여부)
     */
    @Transactional
    public void updateTodoTaskRoutine(String memberId, Long todoTaskId, UpdateTodoTaskRoutineCommand command) {

        TodoTask todoTask = todoTaskRepository
                .getTodoTask(todoTaskId, memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (command.startDate().isBefore(todoTask.getDate())) {
            throw new RestApiException(INVALID_PARAM_ERROR);
        }

        if (!todoTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        List<TodoTask> todoTasks = todoTaskRepository.getTodoTasks(todoTask.getRoutine());

        Set<Holiday> holidaySet = command.isExcludeHolidays()
                ? holidayService.getHolidays(CountryCode.KR, command.startDate(), command.endDate())
                : Set.of();

        TaskRoutineDateCalculator.RoutineDateToModify routineDateToModify =
                routineDateCalculator.calculateRoutineDatesToModify(
                        todoTask,
                        new TaskRoutineDateCalculator.RoutineCondition(
                                command.startDate(),
                                command.endDate(),
                                command.cycle(),
                                command.pattern(),
                                command.isExcludeHolidays()),
                        holidaySet);

        todoTask.createRoutine(
                command.startDate(),
                command.endDate(),
                command.cycle(),
                command.pattern(),
                command.isExcludeHolidays());

        todoTasks.stream()
                .filter(task -> routineDateToModify.toUpdateDates().contains(task.getDate()))
                .forEach(task -> task.updateRoutine(todoTask.getRoutine()));

        todoTaskRepository.saveTodoTasks(TodoTask.of(todoTask, routineDateToModify.toCreateDates()));

        todoTaskRepository.deleteTodoTasks(todoTasks.stream()
                .filter(task -> routineDateToModify.toDeleteDates().contains(task.getDate()))
                .toList());
    }

    /**
     * 투두모드 태스크를 완료합니다.
     *
     * @param memberId   투두모드 태스크를 완료할 사용자의 ID
     * @param todoTaskId 완료할 투두모드 태스크의 ID
     */
    @Transactional
    public TodoTask successTodoTask(String memberId, Long todoTaskId) {
        return todoTaskRepository
                .getTodoTask(todoTaskId, memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST))
                .success();
    }

    /**
     * 완료했던 투두모드 태스크를 취소합니다.
     *
     * @param memberId   투두모드 태스크를 완료 취소할 사용자의 ID
     * @param todoTaskId 완료 취소할 투두모드 태스크의 ID
     */
    @Transactional
    public TodoTask waitTodoTask(String memberId, Long todoTaskId) {
        return todoTaskRepository
                .getTodoTask(todoTaskId, memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST))
                .uncomplete();
    }
}
