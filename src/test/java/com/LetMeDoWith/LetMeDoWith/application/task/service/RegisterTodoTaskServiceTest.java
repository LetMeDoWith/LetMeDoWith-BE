package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateTodoTaskCommand.TodoTaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.application.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
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
    
    @Mock
    private TodoTaskRepository todoTaskRepository;
    
    @Mock
    private TaskCategoryRepository taskCategoryRepository;
    
    @Mock
    private TodoTaskRoutineDateCalculator routineDateCalculator;
    
    @Mock
    private DailyRoutineDateCalculateStrategy dailyRoutineScheduleStrategy;
    
    @Mock
    private Map<String, TodoTaskRoutineDateCalculateStrategy> routineScheduleStrategies;
    
    @Mock
    private HolidayFilter holidayFilter;
    
    @InjectMocks
    private RegisterTodoTaskService registerTodoTaskService;
    
    private CreateTodoTaskCommand command;
    
    @BeforeEach
    void setUp() {
        command = CreateTodoTaskCommand.builder()
                                       .taskCategoryId(1L)
                                       .title("Test Task")
                                       .startDate(LocalDate.now().plusDays(1))
                                       .startTime(LocalTime.of(10, 0))
                                       .build();
    }
    
    @Test
    @DisplayName("[SUCCESS] TodoTask 생성 성공")
    void testRegisterTodoTaskSuccess() {
        // given
        when(taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), 1L))
            .thenReturn(Optional.of(new TaskCategory()));
        
        TodoTask todoTask = TodoTask.of(1L,
                                        command.taskCategoryId(),
                                        command.title(),
                                        command.startDate(),
                                        command.startTime());
        when(todoTaskRepository.saveTodoTask(any(TodoTask.class)))
            .thenReturn(todoTask);
        
        // when
        RegisterTodoTaskResult result = registerTodoTaskService.registerTodoTask(1L, command);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.todoTaskList()).hasSize(1);
        
        // 구체적인 내용 검증
        assertThat(result.todoTaskList().get(0).getTitle()).isEqualTo(command.title());
        assertThat(result.todoTaskList().get(0).getDate()).isEqualTo(command.startDate());
        assertThat(result.todoTaskList().get(0).getStartTime()).isEqualTo(command.startTime());
        assertThat(result.todoTaskList()
                         .get(0)
                         .getTaskCategoryId()).isEqualTo(command.taskCategoryId());
        
        // 메소드 호출 검증
        verify(taskCategoryRepository).getActiveTaskCategory(command.taskCategoryId(), 1L);
        verify(todoTaskRepository).saveTodoTask(any(TodoTask.class));
    }
    
    @Test
    @DisplayName("[FAIL] 존재하지 않는 TaskCategory로 TodoTask 생성 실패")
    void testRegisterTodoTaskFailDueToNonExistentCategory() {
        // given
        when(taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), 1L))
            .thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> registerTodoTaskService.registerTodoTask(1L, command))
            .isInstanceOf(RestApiException.class)
            .hasFieldOrPropertyWithValue("status",
                                         FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST);
    }
    
    @Test
    @DisplayName("[SUCCESS] 루틴 할일 등록 성공")
    void testRegisterTodoTaskRoutineSuccess() {
        // given
        LocalDate startDate = LocalDate.now().plusDays(5); // 현재로부터 5일 후
        LocalDate endDate = startDate.plusMonths(1); // 시작일로부터 1개월 후
        String title = "매일 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.DAILY;
        TodoTaskRoutineCondition routineCondition = TodoTaskRoutineCondition.builder()
                                                                            .cycle(cycle)
                                                                            .pattern(Set.of()) // DAILY는 패턴을 사용하지 않음
                                                                            .isExcludeHolidays(false)
                                                                            .build();
        
        CreateTodoTaskCommand routineCommand = CreateTodoTaskCommand.builder()
                                                                    .taskCategoryId(1L)
                                                                    .title(title)
                                                                    .startDate(startDate)
                                                                    .endDate(endDate)
                                                                    .startTime(LocalTime.now()
                                                                                        .plusHours(1)) // 현재로부터 1시간 후
                                                                    .isRoutine(true)
                                                                    .routineCondition(
                                                                        routineCondition)
                                                                    .build();
        
        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
            .thenReturn(Optional.of(new TaskCategory()));
        
        Set<LocalDate> routineDates = Set.of(startDate,
                                             startDate.plusDays(1),
                                             startDate.plusDays(2));
        when(routineDateCalculator.computeRoutineDates(
            eq(cycle),
            eq(startDate),
            eq(endDate),
            eq(Set.of())))
            .thenReturn(routineDates);
        
        List<TodoTask> todoTasks = TodoTask.ofWithRoutine(1L,
                                                          routineCommand.taskCategoryId(),
                                                          title,
                                                          startDate,
                                                          routineCommand.startTime(),
                                                          routineDates);
        when(todoTaskRepository.saveTodoTasks(any()))
            .thenReturn(todoTasks);
        
        // when
        RegisterTodoTaskResult result = registerTodoTaskService.registerTodoTaskRoutine(1L,
                                                                                        routineCommand);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.todoTaskList()).hasSize(3);
        assertThat(result.todoTaskList().get(0).getTitle()).isEqualTo(title);
        assertThat(result.todoTaskList().get(0).getDate()).isEqualTo(startDate);
        assertThat(result.todoTaskList()
                         .get(0)
                         .getStartTime()).isEqualTo(routineCommand.startTime());
        assertThat(result.todoTaskList()
                         .get(0)
                         .getTaskCategoryId()).isEqualTo(routineCommand.taskCategoryId());
        assertThat(result.todoTaskList().get(0).getIsRoutine()).isTrue();
        assertThat(result.routineDates()).containsExactlyInAnyOrderElementsOf(routineDates);
        
        // 메소드 호출 검증
        verify(taskCategoryRepository).getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE);
        verify(routineDateCalculator).computeRoutineDates(
            eq(cycle),
            eq(startDate),
            eq(endDate),
            eq(Set.of()));
        verify(todoTaskRepository).saveTodoTasks(any());
    }
    
    @Test
    @DisplayName("[FAIL] 존재하지 않는 TaskCategory로 루틴 할일 등록 실패")
    void testRegisterTodoTaskRoutineFailDueToNonExistentCategory() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String title = "매일 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.DAILY;
        TodoTaskRoutineCondition routineCondition = TodoTaskRoutineCondition.builder()
                                                                            .cycle(cycle)
                                                                            .pattern(Set.of())
                                                                            .isExcludeHolidays(false)
                                                                            .build();
        
        CreateTodoTaskCommand routineCommand = CreateTodoTaskCommand.builder()
                                                                    .taskCategoryId(1L)
                                                                    .title(title)
                                                                    .startDate(startDate)
                                                                    .endDate(endDate)
                                                                    .startTime(LocalTime.of(10, 0))
                                                                    .isRoutine(true)
                                                                    .routineCondition(
                                                                        routineCondition)
                                                                    .build();
        
        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
            .thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> registerTodoTaskService.registerTodoTaskRoutine(1L,
                                                                                 routineCommand))
            .isInstanceOf(RestApiException.class)
            .hasFieldOrPropertyWithValue("status",
                                         FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST);
    }
    
    @Test
    @DisplayName("[FAIL] 시작일이 종료일보다 늦은 경우 실패")
    void testRegisterTodoTaskRoutineFailWhenStartDateIsAfterEndDate() {
        // given
        LocalDate startDate = LocalDate.of(2024, 12, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        String title = "매일 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.DAILY;
        TodoTaskRoutineCondition routineCondition = TodoTaskRoutineCondition.builder()
                                                                            .cycle(cycle)
                                                                            .pattern(Set.of())
                                                                            .isExcludeHolidays(false)
                                                                            .build();
        
        CreateTodoTaskCommand routineCommand = CreateTodoTaskCommand.builder()
                                                                    .taskCategoryId(1L)
                                                                    .title(title)
                                                                    .startDate(startDate)
                                                                    .endDate(endDate)
                                                                    .startTime(LocalTime.of(10, 0))
                                                                    .isRoutine(true)
                                                                    .routineCondition(
                                                                        routineCondition)
                                                                    .build();
        
        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
            .thenReturn(Optional.of(new TaskCategory()));
        
        when(routineDateCalculator.computeRoutineDates(
            eq(cycle),
            eq(startDate),
            eq(endDate),
            eq(Set.of())))
            .thenThrow(new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE));
        
        // when & then
        assertThatThrownBy(() -> registerTodoTaskService.registerTodoTaskRoutine(1L,
                                                                                 routineCommand))
            .isInstanceOf(RestApiException.class)
            .hasFieldOrPropertyWithValue("status", FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
    }
    
    @Test
    @DisplayName("[FAIL] 패턴이 사이클과 일치하지 않는 경우 실패")
    void testRegisterTodoTaskRoutineFailWhenPatternDoesNotMatchCycle() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String title = "매주 운동하기";
        TodoTaskRoutineCycle cycle = TodoTaskRoutineCycle.WEEKLY;
        TodoTaskRoutineCondition routineCondition = TodoTaskRoutineCondition.builder()
                                                                            .cycle(cycle)
                                                                            .pattern(Set.of(1,
                                                                                            2,
                                                                                            3,
                                                                                            4,
                                                                                            5,
                                                                                            6,
                                                                                            7,
                                                                                            8)) // 8은 유효하지 않은 요일
                                                                            .isExcludeHolidays(false)
                                                                            .build();
        
        CreateTodoTaskCommand routineCommand = CreateTodoTaskCommand.builder()
                                                                    .taskCategoryId(1L)
                                                                    .title(title)
                                                                    .startDate(startDate)
                                                                    .endDate(endDate)
                                                                    .startTime(LocalTime.of(10, 0))
                                                                    .isRoutine(true)
                                                                    .routineCondition(
                                                                        routineCondition)
                                                                    .build();
        
        when(taskCategoryRepository.getTaskCategory(routineCommand.taskCategoryId(), Yn.TRUE))
            .thenReturn(Optional.of(new TaskCategory()));
        
        when(routineDateCalculator.computeRoutineDates(
            eq(cycle),
            eq(startDate),
            eq(endDate),
            eq(Set.of(1, 2, 3, 4, 5, 6, 7, 8))))
            .thenThrow(new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE));
        
        // when & then
        assertThatThrownBy(() -> registerTodoTaskService.registerTodoTaskRoutine(1L,
                                                                                 routineCommand))
            .isInstanceOf(RestApiException.class)
            .hasFieldOrPropertyWithValue("status", FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
    }
}