package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskCommand.TodoTaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineDateCalculator;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.strategy.DailyRoutineDateCalculateStrategy;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.strategy.TodoTaskRoutineDateCalculateStrategy;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterTodoTaskServiceTest {

    @Mock private TodoTaskRepository todoTaskRepository;

    @Mock private TaskCategoryRepository taskCategoryRepository;

    @Mock private TodoTaskRoutineDateCalculator routineDateCalculator;

    @Mock private DailyRoutineDateCalculateStrategy dailyRoutineScheduleStrategy;

    @Mock private Map<String, TodoTaskRoutineDateCalculateStrategy> routineScheduleStrategies;

    @Mock private HolidayService holidayService;

    @InjectMocks private RegisterTodoTaskService registerTodoTaskService;

    private RegisterTodoTaskCommand command;

    @BeforeEach
    void setUp() {
        command =
                RegisterTodoTaskCommand.builder()
                        .taskCategoryId(1L)
                        .title("Test Task")
                        .startDate(SystemTimeUtil.nowDate().plusDays(1))
                        .startTime(LocalTime.of(10, 0))
                        .build();
    }

    @Test
    @DisplayName("[SUCCESS] 루틴이 아닌 TodoTask 생성 성공")
    void testRegisterTodoTaskSuccess() {
        // given
        when(taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), 1L))
                .thenReturn(Optional.of(new TaskCategory()));
        when(todoTaskRepository.saveTodoTask(any(TodoTask.class)))
                .thenReturn(
                        TodoTask.of(
                                1L, 1L, "Test Task", SystemTimeUtil.nowDate().plusDays(1), LocalTime.of(10, 0)));

        // when
        RegisterTodoTaskResult result = registerTodoTaskService.registerTodoTask(1L, command);

        // then
        assertThat(result).isNotNull();
        verify(todoTaskRepository).saveTodoTask(any(TodoTask.class));
    }

    @Test
    @DisplayName("[SUCCESS] 루틴 TodoTask 생성 성공")
    void testRegisterTodoTaskWithRoutineSuccess() {
        // given
        LocalDate startDate = SystemTimeUtil.nowDate().plusDays(1);
        LocalDate endDate = startDate.plusDays(7);
        String title = "매일 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.DAILY;
        TodoTaskRoutineCondition routineCondition =
                TodoTaskRoutineCondition.builder()
                        .cycle(cycle)
                        .pattern(Set.of())
                        .isExcludeHolidays(false)
                        .build();

        RegisterTodoTaskCommand routineCommand =
                RegisterTodoTaskCommand.builder()
                        .taskCategoryId(1L)
                        .title(title)
                        .startDate(startDate)
                        .endDate(endDate)
                        .startTime(SystemTimeUtil.nowTime().plusHours(1))
                        .isRoutine(true)
                        .routineCondition(routineCondition)
                        .build();

        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
                .thenReturn(Optional.of(new TaskCategory()));

        Set<LocalDate> routineDates = Set.of(startDate, startDate.plusDays(1), startDate.plusDays(2));
        when(routineDateCalculator.computeRoutineDates(
                        eq(cycle), eq(startDate), eq(endDate), eq(Set.of())))
                .thenReturn(routineDates);

        List<TodoTask> todoTasks =
                TodoTask.ofWithRoutine(
                        1L, 1L, title, startDate, SystemTimeUtil.nowTime().plusHours(1), routineDates);
        when(todoTaskRepository.saveTodoTasks(any(List.class))).thenReturn(todoTasks);

        // when
        RegisterTodoTaskResult result =
                registerTodoTaskService.registerTodoTaskWithRoutine(1L, routineCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.todoTaskList()).hasSize(3);
        verify(todoTaskRepository).saveTodoTasks(any(List.class));
    }

    @Test
    @DisplayName("[SUCCESS] 공휴일 제외 루틴 TodoTask 생성 성공")
    void testRegisterTodoTaskWithRoutineWithHolidayExclusionSuccess() {
        // given
        LocalDate startDate = SystemTimeUtil.nowDate().plusDays(1);
        LocalDate endDate = startDate.plusDays(7);
        String title = "매일 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.DAILY;
        TodoTaskRoutineCondition routineCondition =
                TodoTaskRoutineCondition.builder()
                        .cycle(cycle)
                        .pattern(Set.of())
                        .isExcludeHolidays(true)
                        .build();

        RegisterTodoTaskCommand routineCommand =
                RegisterTodoTaskCommand.builder()
                        .taskCategoryId(1L)
                        .title(title)
                        .startDate(startDate)
                        .endDate(endDate)
                        .startTime(SystemTimeUtil.nowTime().plusHours(1))
                        .isRoutine(true)
                        .routineCondition(routineCondition)
                        .build();

        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
                .thenReturn(Optional.of(new TaskCategory()));

        Set<LocalDate> routineDates = Set.of(startDate, startDate.plusDays(1), startDate.plusDays(2));
        when(routineDateCalculator.computeRoutineDates(
                        eq(cycle), eq(startDate), eq(endDate), eq(Set.of())))
                .thenReturn(routineDates);

        Set<LocalDate> holidays = Set.of(startDate.plusDays(1));
        when(holidayService.getHolidays(CountryCode.KR, startDate, endDate)).thenReturn(holidays);

        List<TodoTask> todoTasks =
                TodoTask.ofWithRoutine(
                        1L,
                        1L,
                        title,
                        startDate,
                        SystemTimeUtil.nowTime().plusHours(1),
                        routineDates,
                        holidays);
        when(todoTaskRepository.saveTodoTasks(any(List.class))).thenReturn(todoTasks);

        // when
        RegisterTodoTaskResult result =
                registerTodoTaskService.registerTodoTaskWithRoutine(1L, routineCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.todoTaskList()).hasSize(2);
        verify(todoTaskRepository).saveTodoTasks(any(List.class));
    }

    @Test
    @DisplayName("[FAIL] 존재하지 않는 카테고리로 TodoTask 생성 시도 시 실패")
    void testRegisterTodoTaskFailWhenCategoryNotExist() {
        // given
        when(taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), 1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> registerTodoTaskService.registerTodoTask(1L, command))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue(
                        "status", FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST);
    }

    @Test
    @DisplayName("[FAIL] 존재하지 않는 카테고리로 루틴 TodoTask 생성 시도 시 실패")
    void testRegisterTodoTaskWithRoutineFailWhenCategoryNotExist() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String title = "매일 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.DAILY;
        TodoTaskRoutineCondition routineCondition =
                TodoTaskRoutineCondition.builder()
                        .cycle(cycle)
                        .pattern(Set.of())
                        .isExcludeHolidays(false)
                        .build();

        RegisterTodoTaskCommand routineCommand =
                RegisterTodoTaskCommand.builder()
                        .taskCategoryId(1L)
                        .title(title)
                        .startDate(startDate)
                        .endDate(endDate)
                        .startTime(LocalTime.of(10, 0))
                        .isRoutine(true)
                        .routineCondition(routineCondition)
                        .build();

        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                        () -> registerTodoTaskService.registerTodoTaskWithRoutine(1L, routineCommand))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("status", FailResponseStatus.INVALID_REQUEST);
    }

    @Test
    @DisplayName("[FAIL] 시작일이 종료일보다 늦은 경우 실패")
    void testRegisterTodoTaskWithRoutineFailWhenStartDateIsAfterEndDate() {
        // given
        LocalDate startDate = LocalDate.of(2024, 12, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        String title = "매일 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.DAILY;
        TodoTaskRoutineCondition routineCondition =
                TodoTaskRoutineCondition.builder()
                        .cycle(cycle)
                        .pattern(Set.of())
                        .isExcludeHolidays(false)
                        .build();

        RegisterTodoTaskCommand routineCommand =
                RegisterTodoTaskCommand.builder()
                        .taskCategoryId(1L)
                        .title(title)
                        .startDate(startDate)
                        .endDate(endDate)
                        .startTime(LocalTime.of(10, 0))
                        .isRoutine(true)
                        .routineCondition(routineCondition)
                        .build();

        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
                .thenReturn(Optional.of(new TaskCategory()));

        // when & then
        assertThatThrownBy(
                        () -> registerTodoTaskService.registerTodoTaskWithRoutine(1L, routineCommand))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("status", FailResponseStatus.INVALID_REQUEST);
    }

    @Test
    @DisplayName("[FAIL] 패턴이 사이클과 일치하지 않는 경우 실패")
    void testRegisterTodoTaskWithRoutineFailWhenPatternDoesNotMatchCycle() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String title = "매주 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.WEEKLY;
        TodoTaskRoutineCondition routineCondition =
                TodoTaskRoutineCondition.builder()
                        .cycle(cycle)
                        .pattern(Set.of(1, 2, 3, 4, 5, 6, 7, 8))
                        .isExcludeHolidays(false)
                        .build();

        RegisterTodoTaskCommand routineCommand =
                RegisterTodoTaskCommand.builder()
                        .taskCategoryId(1L)
                        .title(title)
                        .startDate(startDate)
                        .endDate(endDate)
                        .startTime(LocalTime.of(10, 0))
                        .isRoutine(true)
                        .routineCondition(routineCondition)
                        .build();

        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
                .thenReturn(Optional.of(new TaskCategory()));

        when(routineDateCalculator.computeRoutineDates(
                        eq(cycle), eq(startDate), eq(endDate), eq(Set.of(1, 2, 3, 4, 5, 6, 7, 8))))
                .thenThrow(new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE));

        // when & then
        assertThatThrownBy(
                        () -> registerTodoTaskService.registerTodoTaskWithRoutine(1L, routineCommand))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("status", FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
    }
}
