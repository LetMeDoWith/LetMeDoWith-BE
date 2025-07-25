package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineDateCalculator;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TodoTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTasksResDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class DeleteTodoTaskIntegrationTest extends AbstractIntegrationTest {

    static final String URL = "/api/v1/tasks/todo";

    // Test time constants
    private static final LocalDateTime FIXED_CLOCK_TIME = LocalDateTime.of(2024, 6, 1, 0, 0);
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 6, 2);
    private static final LocalDate ROUTINE_END_DATE = LocalDate.of(2024, 6, 30);
    private static final LocalDate SAMPLE_DATE = LocalDate.of(2024, 6, 15);
    private static final LocalTime TEST_START_TIME = LocalTime.of(9, 0);

    // Test string constants
    private static final String TEST_TITLE = "테스트 태스크";

    // API constants
    private static final String RETRIEVE_TASKS_URL = "/api/v1/tasks";
    private static final String YEAR_PARAM = "2024";
    private static final String MONTH_PARAM = "6";

    @Autowired
    private TodoTaskJpaRepository todoTaskRepository;

    @Autowired
    private TaskCategoryJpaRepository taskCategoryRepository;

    @Autowired
    private TodoTaskRoutineDateCalculator todoTaskRoutineDateCalculator;

    private TaskCategory taskCategory;

    @Override
    protected void deleteTestData() {
        todoTaskRepository.deleteAll();
        taskCategoryRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        taskCategory = taskCategoryRepository.save(TaskCategory.of(
                "test category", TaskCategory.TaskCategoryCreationType.COMMON, "test", this.requestMember.getId()));
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 단일 태스크 삭제 - 루틴이 아닌 태스크")
    void deleteSingleTodoTaskTest() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        TodoTask todoTask = todoTaskRepository.save(
                TodoTask.of(this.requestMember.getId(), taskCategory.getId(), TEST_TITLE, TEST_DATE, TEST_START_TIME));

        // when
        ResultActions resultActions = this.request(MockMvcRequestBuilders.delete(URL + "/" + todoTask.getId()));

        // then
        resultActions.andExpect(status().isOk());

        // 조회 API를 통해 삭제 검증 - CQRS 패턴
        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);

        // 태스크가 삭제되어 목록에 없어야 함
        boolean taskExists =
                tasks.todoTasks().stream().anyMatch(task -> task.id().equals(todoTask.getId()));
        assertThat(taskExists).isFalse();
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 루틴 태스크 개별 삭제 - 루틴에서 하나만 삭제")
    void deleteRoutineTodoTaskIndividualTest() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        Set<LocalDate> routineDates = todoTaskRoutineDateCalculator.computeRoutineDates(
                TodoTaskRoutineCycle.DAILY, TEST_DATE, ROUTINE_END_DATE, null);

        List<TodoTask> todoTasks = TodoTask.ofWithRoutine(
                this.requestMember.getId(),
                taskCategory.getId(),
                TEST_TITLE,
                TEST_START_TIME,
                routineDates,
                TodoTaskRoutineCycle.DAILY,
                null,
                false);

        List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(todoTasks);

        // 중간에 있는 태스크를 선택해서 삭제
        TodoTask taskToDelete = savedTodoTasks.stream()
                .filter(task -> task.getDate().isEqual(SAMPLE_DATE))
                .findFirst()
                .orElseThrow();

        int originalTaskCount = savedTodoTasks.size();

        // when
        ResultActions resultActions = this.request(MockMvcRequestBuilders.delete(URL + "/" + taskToDelete.getId()));

        // then
        resultActions.andExpect(status().isOk());

        // 조회 API를 통해 삭제 검증 - CQRS 패턴
        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);

        // 삭제된 태스크는 목록에 없어야 함
        boolean deletedTaskExists =
                tasks.todoTasks().stream().anyMatch(task -> task.id().equals(taskToDelete.getId()));
        assertThat(deletedTaskExists).isFalse();

        // 나머지 루틴 태스크들은 여전히 존재해야 함 (개별 삭제이므로)
        long remainingTaskCount = tasks.todoTasks().stream()
                .filter(task -> task.title().equals(TEST_TITLE))
                .count();
        assertThat(remainingTaskCount).isEqualTo(originalTaskCount - 1);
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 루틴 태스크 전체 삭제 - 루틴 포함 모든 태스크 삭제")
    void deleteRoutineTodoTaskWithRoutineTest() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        Set<LocalDate> routineDates = todoTaskRoutineDateCalculator.computeRoutineDates(
                TodoTaskRoutineCycle.DAILY, TEST_DATE, ROUTINE_END_DATE, null);

        List<TodoTask> todoTasks = TodoTask.ofWithRoutine(
                this.requestMember.getId(),
                taskCategory.getId(),
                TEST_TITLE,
                TEST_START_TIME,
                routineDates,
                TodoTaskRoutineCycle.DAILY,
                null,
                false);

        List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(todoTasks);

        // 중간에 있는 태스크를 선택 (기준 태스크)
        TodoTask pivotTask = savedTodoTasks.stream()
                .filter(task -> task.getDate().isEqual(SAMPLE_DATE))
                .findFirst()
                .orElseThrow();

        // when - with-routine 엔드포인트 사용
        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.delete(URL + "/" + pivotTask.getId() + "/with-routine"));

        // then
        resultActions.andExpect(status().isOk());

        // 조회 API를 통해 삭제 검증 - CQRS 패턴
        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);

        // pivot 태스크 이후의 모든 루틴 태스크들이 삭제되어야 함
        // (TodoTaskRoutineSplitter 로직에 따라 pivot 이후는 삭제, 이전은 루틴에서 분리)
        long remainingRoutineTaskCount = tasks.todoTasks().stream()
                .filter(task -> task.title().equals(TEST_TITLE))
                .filter(task -> !task.date().isBefore(pivotTask.getDate())) // pivot 이후
                .count();
        assertThat(remainingRoutineTaskCount).isEqualTo(0);

        // pivot 이전의 태스크들은 루틴에서 분리되어 개별 태스크로 남아있어야 함
        long detachedTaskCount = tasks.todoTasks().stream()
                .filter(task -> task.title().equals(TEST_TITLE))
                .filter(task -> task.date().isBefore(pivotTask.getDate())) // pivot 이전
                .count();

        long expectedDetachedCount = savedTodoTasks.stream()
                .filter(task -> task.getDate().isBefore(pivotTask.getDate()))
                .count();
        assertThat(detachedTaskCount).isEqualTo(expectedDetachedCount);
    }
}
