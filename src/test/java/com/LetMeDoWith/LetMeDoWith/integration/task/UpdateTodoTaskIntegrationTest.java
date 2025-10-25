package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TaskRoutineDateCalculator;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TodoTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTasksResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTasksResDto.TodoTaskDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateTodoTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateTodoTaskRoutineReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateTodoTaskWithRoutineReqDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class UpdateTodoTaskIntegrationTest extends AbstractIntegrationTest {

    static final String URL = "/api/v1/tasks/todo";

    // Test time constants
    private static final LocalDateTime FIXED_CLOCK_TIME = LocalDateTime.of(2024, 6, 1, 0, 0);
    private static final LocalDate ORIGINAL_DATE = LocalDate.of(2024, 6, 2);
    private static final LocalDate ROUTINE_END_DATE = LocalDate.of(2024, 6, 30);
    private static final LocalDate SAMPLE_DATE = LocalDate.of(2024, 6, 15);
    private static final LocalTime ORIGINAL_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime UPDATED_START_TIME = LocalTime.of(10, 0);

    // Test string constants
    private static final String ORIGINAL_TITLE = "원래 타이틀";
    private static final String UPDATED_TITLE = "수정된 타이틀";

    // API constants
    private static final String RETRIEVE_TASKS_URL = "/api/v1/tasks";
    private static final String YEAR_PARAM = "2024";
    private static final String MONTH_PARAM = "6";

    @Autowired
    private TodoTaskJpaRepository todoTaskRepository;

    @Autowired
    private TaskCategoryJpaRepository taskCategoryRepository;

    @Autowired
    private TaskRoutineDateCalculator taskRoutineDateCalculator;

    private TaskCategory taskCategory;
    private TaskCategory taskCategory2;

    @Override
    protected void deleteTestData() {
        todoTaskRepository.deleteAll();
        taskCategoryRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        taskCategory = taskCategoryRepository.save(TaskCategory.of(
                "test category 1", TaskCategory.TaskCategoryCreationType.COMMON, "test", this.requestMember.getId()));

        taskCategory2 = taskCategoryRepository.save(TaskCategory.of(
                "test category 2", TaskCategory.TaskCategoryCreationType.COMMON, "test", this.requestMember.getId()));
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 단일 Task 수정 - 루틴이 아닌 태스크 수정")
    void updateSingleTodoTaskTestContentOnly() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        TodoTask todoTask = todoTaskRepository.save(TodoTask.of(
                this.requestMember.getId(), taskCategory.getId(), ORIGINAL_TITLE, ORIGINAL_DATE, ORIGINAL_START_TIME));

        // when
        LocalDateTime updateStartDateTime = LocalDateTime.of(
                ORIGINAL_DATE.getYear(),
                ORIGINAL_DATE.getMonth(),
                ORIGINAL_DATE.getDayOfMonth(),
                UPDATED_START_TIME.getHour(),
                UPDATED_START_TIME.getMinute());
        Long updatedCategoryId = taskCategory2.getId();
        UpdateTodoTaskReqDto updateReq = new UpdateTodoTaskReqDto(
                UPDATED_TITLE,
                updatedCategoryId,
                updateStartDateTime.toLocalDate(),
                updateStartDateTime.toLocalTime(),
                null);

        ResultActions resultActions = this.request(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(URL + "/" + todoTask.getId())
                        .content(this.writeRequestBodyAsString(updateReq)));

        // then
        resultActions.andExpect(status().isOk());

        // 조회 API를 통해 변경사항 검증
        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);

        tasks.todoTasks().stream()
                .filter(task -> task.id().equals(todoTask.getId()))
                .findFirst()
                .ifPresent(task -> {
                    assertThat(task.title()).isEqualTo(UPDATED_TITLE);
                    assertThat(task.startTime()).isEqualTo(UPDATED_START_TIME);
                    assertThat(task.taskCategoryId()).isEqualTo(updatedCategoryId);
                });
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 단일 Task 수정 - 루틴으로 변경")
    void updateSingleTodoTaskTestConvertToRoutine() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        TodoTask todoTask = todoTaskRepository.save(TodoTask.of(
                this.requestMember.getId(), taskCategory.getId(), ORIGINAL_TITLE, ORIGINAL_DATE, ORIGINAL_START_TIME));

        // when
        LocalDateTime updateStartDateTime = LocalDateTime.of(
                ORIGINAL_DATE.getYear(),
                ORIGINAL_DATE.getMonth(),
                ORIGINAL_DATE.getDayOfMonth(),
                UPDATED_START_TIME.getHour(),
                UPDATED_START_TIME.getMinute());
        Long updatedCategoryId = taskCategory2.getId();
        UpdateTodoTaskReqDto updateReq = new UpdateTodoTaskReqDto(
                UPDATED_TITLE,
                updatedCategoryId,
                updateStartDateTime.toLocalDate(),
                updateStartDateTime.toLocalTime(),
                UpdateTodoTaskRoutineReqDto.of(ORIGINAL_DATE, ROUTINE_END_DATE, TaskRoutineCycle.DAILY, null, false));

        long gap = ChronoUnit.DAYS.between(ORIGINAL_DATE, ROUTINE_END_DATE);

        ResultActions resultActions = this.request(MockMvcRequestBuilders.put(URL + "/" + todoTask.getId())
                .content(this.writeRequestBodyAsString(updateReq)));

        // then
        resultActions.andExpect(status().isOk());

        // 조회 API를 통해 변경사항 검증
        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);

        List<TodoTaskDto> todoTasks = tasks.todoTasks();

        Assertions.assertEquals(gap + 1, todoTasks.size(), "루틴으로 변환된 Task는 29개여야 합니다.");
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 단일 Task 수정 - 루틴에 속하는 태스크 수정")
    void updateTodoTaskRoutineContentTestApplyRequestedTaskOnly() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        Set<LocalDate> routineDates = taskRoutineDateCalculator.computeRoutineDates(
                TaskRoutineCycle.DAILY, ORIGINAL_DATE, ROUTINE_END_DATE, null);

        TodoTask todoTask = TodoTask.ofWithRoutine(
                this.requestMember.getId(),
                taskCategory.getId(),
                ORIGINAL_TITLE,
                ORIGINAL_DATE,
                ORIGINAL_START_TIME,
                ORIGINAL_DATE,
                ROUTINE_END_DATE,
                TaskRoutineCycle.DAILY,
                null,
                false);

        List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(TodoTask.of(todoTask, routineDates));

        // when
        LocalDateTime updateStartDateTime = LocalDateTime.of(
                ORIGINAL_DATE.getYear(),
                ORIGINAL_DATE.getMonth(),
                ORIGINAL_DATE.getDayOfMonth(),
                UPDATED_START_TIME.getHour(),
                UPDATED_START_TIME.getMinute());
        Long updatedCategoryId = taskCategory2.getId();

        TodoTask taskToModified = savedTodoTasks.stream()
                .filter(task -> task.getDate().isEqual(SAMPLE_DATE))
                .findFirst()
                .get();

        UpdateTodoTaskReqDto req = new UpdateTodoTaskReqDto(
                UPDATED_TITLE,
                updatedCategoryId,
                updateStartDateTime.toLocalDate(),
                updateStartDateTime.toLocalTime(),
                null);

        ResultActions resultActions = this.request(MockMvcRequestBuilders.put(URL + "/" + taskToModified.getId())
                .content(this.writeRequestBodyAsString(req)));

        // then
        resultActions.andExpect(status().isOk());

        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);

        List<TodoTaskDto> retrievedTodoTasks = tasks.todoTasks();

        retrievedTodoTasks.stream()
                .filter(task -> task.id().equals(taskToModified.getId()))
                .findFirst()
                .ifPresent(task -> {
                    assertThat(task.title()).isEqualTo(UPDATED_TITLE);
                    assertThat(task.startTime()).isEqualTo(UPDATED_START_TIME);
                    assertThat(task.taskCategoryId()).isEqualTo(taskCategory2.getId());
                });

        assertThat(retrievedTodoTasks.stream()
                        .filter(task -> !task.id().equals(taskToModified.getId()))
                        .allMatch(task -> task.title().equals(ORIGINAL_TITLE)
                                && task.startTime().equals(ORIGINAL_START_TIME)
                                && task.taskCategoryId().equals(taskCategory.getId())))
                .isTrue();
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 루틴 컨텐츠 수정 - 모두 적용")
    void updateTodoTaskRoutineContentTestApplyAllRoutineTasks() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        Set<LocalDate> routineDates = taskRoutineDateCalculator.computeRoutineDates(
                TaskRoutineCycle.DAILY, ORIGINAL_DATE, ROUTINE_END_DATE, null);

        TodoTask todoTask = TodoTask.ofWithRoutine(
                this.requestMember.getId(),
                taskCategory.getId(),
                ORIGINAL_TITLE,
                ORIGINAL_DATE,
                ORIGINAL_START_TIME,
                ORIGINAL_DATE,
                ROUTINE_END_DATE,
                TaskRoutineCycle.DAILY,
                null,
                false);

        List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(TodoTask.of(todoTask, routineDates));

        // when
        LocalDateTime updatedStartDateTime = LocalDateTime.of(
                ORIGINAL_DATE.getYear(),
                ORIGINAL_DATE.getMonth(),
                ORIGINAL_DATE.getDayOfMonth(),
                UPDATED_START_TIME.getHour(),
                UPDATED_START_TIME.getMinute());

        // 모두 적용에서 의미는 없지만 ID가 필요하므로 Task를 가져옴
        TodoTask taskToModified = savedTodoTasks.stream()
                .filter(task -> task.getDate().isEqual(SAMPLE_DATE))
                .findFirst()
                .get();

        UpdateTodoTaskWithRoutineReqDto req = new UpdateTodoTaskWithRoutineReqDto(
                UPDATED_TITLE, updatedStartDateTime.toLocalTime(), taskCategory2.getId());

        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.put(URL + "/" + taskToModified.getId() + "/with-routine")
                        .content(this.writeRequestBodyAsString(req)));

        // then
        resultActions.andExpect(status().isOk());

        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);

        List<TodoTaskDto> retrievedTodoTasks = tasks.todoTasks();

        // 기준 일자 이후는 모두 수정되어야 함
        retrievedTodoTasks.stream()
                .filter(task -> task.date().isAfter(taskToModified.getDate())
                        || task.date().isEqual(taskToModified.getDate()))
                .forEach(task -> {
                    assertThat(task.title()).isEqualTo(UPDATED_TITLE);
                    assertThat(task.startTime()).isEqualTo(UPDATED_START_TIME);
                    assertThat(task.taskCategoryId()).isEqualTo(taskCategory2.getId());
                });

        // 기준 일자 이전은 변경 없음
        retrievedTodoTasks.stream()
                .filter(task -> task.date().isBefore(taskToModified.getDate()))
                .forEach(task -> {
                    assertThat(task.title()).isEqualTo(ORIGINAL_TITLE);
                    assertThat(task.startTime()).isEqualTo(ORIGINAL_START_TIME);
                    assertThat(task.taskCategoryId()).isEqualTo(taskCategory.getId());
                });
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 루틴 조건 수정")
    void updateTodoTaskRoutineConditionTest() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        Set<LocalDate> routineDates = taskRoutineDateCalculator.computeRoutineDates(
                TaskRoutineCycle.DAILY, ORIGINAL_DATE, ROUTINE_END_DATE, null);

        TodoTask todoTask = TodoTask.ofWithRoutine(
                this.requestMember.getId(),
                taskCategory.getId(),
                ORIGINAL_TITLE,
                ORIGINAL_DATE,
                ORIGINAL_START_TIME,
                ORIGINAL_DATE,
                ROUTINE_END_DATE,
                TaskRoutineCycle.DAILY,
                null,
                false);

        List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(TodoTask.of(todoTask, routineDates));

        // when
        Set<Integer> updatedPattern = Set.of(20, 21, 22); // 20, 21, 22일로 변경

        // ID가 필요하므로 Task를 가져옴
        TodoTask sample = savedTodoTasks.stream()
                .filter(task -> task.getDate().isEqual(SAMPLE_DATE))
                .findFirst()
                .get();

        UpdateTodoTaskRoutineReqDto req = UpdateTodoTaskRoutineReqDto.of(
                SAMPLE_DATE, ROUTINE_END_DATE, TaskRoutineCycle.MONTHLY, updatedPattern, false
                // 공휴일 제외 여부는 false로 유지
                );

        ResultActions resultActions = this.request(MockMvcRequestBuilders.put(URL + "/" + sample.getId() + "/routine")
                .content(this.writeRequestBodyAsString(req)));

        // then
        resultActions.andExpect(status().isOk());

        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);

        List<TodoTaskDto> retrievedTodoTasks = tasks.todoTasks();

        retrievedTodoTasks.stream()
                .filter(task -> task.date().isBefore(sample.getDate()))
                .forEach(task -> {
                    assertThat(task.title()).isEqualTo(ORIGINAL_TITLE);
                    assertThat(task.startTime()).isEqualTo(ORIGINAL_START_TIME);
                    assertThat(task.taskCategoryId()).isEqualTo(taskCategory.getId());
                });

        assertThat(retrievedTodoTasks.stream()
                        .filter(task -> task.date().isEqual(sample.getDate())
                                || task.date().isAfter(sample.getDate()))
                        .count())
                .isEqualTo(3);
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 태스크 완료")
    void successTodoTaskTest() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        TodoTask todoTask = todoTaskRepository.save(TodoTask.of(
                this.requestMember.getId(), taskCategory.getId(), ORIGINAL_TITLE, ORIGINAL_DATE, ORIGINAL_START_TIME));

        // when
        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.patch(URL + "/" + todoTask.getId() + "/success"));

        // then
        resultActions.andExpect(status().isOk());

        // API 응답에서 완료된 태스크 ID가 반환되는지 확인
        MvcResult result = resultActions.andReturn();
        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();

        // 응답 데이터에서 완료된 태스크 ID 확인
        assertThat(content).contains("\"data\":" + todoTask.getId());

        // 조회 API를 통해 상태 변경 검증
        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse retrieveResponse = retrieveResult.getResponse();
        retrieveResponse.setCharacterEncoding("UTF-8");
        String retrieveContent = retrieveResponse.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(retrieveContent, RetrieveTasksResDto.class);

        // 태스크가 COMPLETE 상태로 변경되었는지 확인
        tasks.todoTasks().stream()
                .filter(task -> task.id().equals(todoTask.getId()))
                .findFirst()
                .ifPresent(task -> {
                    assertThat(task.title()).isEqualTo(ORIGINAL_TITLE);
                    assertThat(task.status()).isEqualTo("SUCCESS");
                    assertThat(task.date()).isEqualTo(ORIGINAL_DATE);
                    assertThat(task.startTime()).isEqualTo(ORIGINAL_START_TIME);
                });
    }

    @Test
    @DisplayName("[SUCCESS] 투두모드 태스크 완료 취소 (대기 상태로 변경)")
    void waitTodoTaskTest() throws Exception {
        // given
        setFixedClock(FIXED_CLOCK_TIME);

        // 완료된 상태의 태스크를 생성하기 위해 먼저 생성 후 완료 처리
        TodoTask todoTask = todoTaskRepository.save(TodoTask.of(
                this.requestMember.getId(), taskCategory.getId(), ORIGINAL_TITLE, ORIGINAL_DATE, ORIGINAL_START_TIME));

        // 태스크를 완료 상태로 변경 (테스트를 위해 수동으로 처리)
        todoTask.success();
        todoTaskRepository.save(todoTask);

        // when
        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.patch(URL + "/" + todoTask.getId() + "/wait"));

        // then
        resultActions.andExpect(status().isOk());

        // API 응답에서 대기 상태로 변경된 태스크 ID가 반환되는지 확인
        MvcResult result = resultActions.andReturn();
        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();

        // 응답 데이터에서 태스크 ID 확인
        assertThat(content).contains("\"data\":" + todoTask.getId());

        // 조회 API를 통해 상태 변경 검증
        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", YEAR_PARAM)
                        .param("month", MONTH_PARAM))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse retrieveResponse = retrieveResult.getResponse();
        retrieveResponse.setCharacterEncoding("UTF-8");
        String retrieveContent = retrieveResponse.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(retrieveContent, RetrieveTasksResDto.class);

        // 태스크가 WAIT 상태로 변경되었는지 확인
        tasks.todoTasks().stream()
                .filter(task -> task.id().equals(todoTask.getId()))
                .findFirst()
                .ifPresent(task -> {
                    assertThat(task.title()).isEqualTo(ORIGINAL_TITLE);
                    assertThat(task.status()).isEqualTo("WAIT");
                    assertThat(task.date()).isEqualTo(ORIGINAL_DATE);
                    assertThat(task.startTime()).isEqualTo(ORIGINAL_START_TIME);
                });
    }
}
