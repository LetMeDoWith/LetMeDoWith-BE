package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskWithRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
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
        todoTask.updateContent(command.title(), category.getId(), command.startTime());

        // 루틴에 포함되는 TodoTask의 경우, 루틴을 분리한다.
        if (todoTask.isRoutine()) {
            todoTask.detachRoutine();
        }

        // 루틴이 아닌 태스크를 루틴으로 변경하는 경우
        if (command.routineCondition().isPresent()) {
            TaskRoutineCondition routineCondition = command.routineCondition().get();

            Set<LocalDate> holidays = routineCondition.isExcludeHolidays()
                    ? holidayService.getHolidays(
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
     * @param memberId   TodoTask를 업데이트할 사용자의 ID
     * @param todoTaskId 업데이트할 TodoTask의 ID
     * @param command    업데이트할 정보 (카테고리 ID, 제목, 시작시간, 전체적용 여부)
     */
    @Transactional
    public void updateTodoTaskWithRoutine(String memberId, Long todoTaskId, UpdateTodoTaskWithRoutineCommand command) {
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

        if (!todoTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }
        TodoTaskRoutine originalRoutine = todoTask.getRoutine();
        List<TodoTask> todoTasksInRoutine = todoTaskRepository.getTodoTasks(originalRoutine);

        // 루틴 분할
        TodoTaskRoutineSplitResult splitResult =
                splitter.splitTodoTaskRoutine(todoTasksInRoutine, todoTask, originalRoutine);

        Set<LocalDate> holidays = command.isExcludeHolidays()
                ? holidayService.getHolidays(CountryCode.KR, command.startDate(), command.endDate())
                : Set.of();

        // 입력받은 TodoTask를 시작으로 루틴 일자 다시 계산해서 루틴 재생성
        Set<LocalDate> newRoutineDates = routineDateCalculator.computeRoutineDates(
                command.cycle(), command.startDate(), command.endDate(), command.pattern(), holidays);

        // 루틴의 조건을 재 설정해도, 수정 요청한 Task는 포함.
        newRoutineDates.add(todoTask.getDate());

        // 기존 루틴에서 새롭게 계산된 루틴 일자와 겹치지 않는 날짜들은 삭제되어야 한다.
        Set<LocalDate> originalRoutineDates = todoTask.getUpdateAvailableDates();
        originalRoutineDates.removeAll(newRoutineDates);

        List<TodoTask> todoTasksToDelete = todoTasksInRoutine.stream()
                .filter(task -> originalRoutineDates.contains(task.getDate()))
                .toList();

        todoTaskRepository.deleteTodoTasks(todoTasksToDelete);

        // 분할된 루틴의 조건 업데이트
        splitResult
                .getNewRoutine()
                .update(
                        command.startDate(),
                        command.endDate(),
                        command.cycle(),
                        command.pattern(),
                        command.isExcludeHolidays());
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
