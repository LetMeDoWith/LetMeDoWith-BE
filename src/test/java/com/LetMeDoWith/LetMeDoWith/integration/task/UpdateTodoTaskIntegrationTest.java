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
    
    @Autowired
    private TodoTaskJpaRepository todoTaskRepository;
    @Autowired
    private TaskCategoryJpaRepository taskCategoryRepository;
    @Autowired
    private TodoTaskRoutineDateCalculator todoTaskRoutineDateCalculator;
    
    private TaskCategory taskCategory;
    private TaskCategory taskCategory2;
    
    @Override
    protected void deleteTestData() {
        todoTaskRepository.deleteAll();
        taskCategoryRepository.deleteAll();
    }
    
    @Override
    protected void createTestData() {
        taskCategory =
            taskCategoryRepository.save(
                TaskCategory.of(
                    "test category 1",
                    TaskCategory.TaskCategoryCreationType.COMMON,
                    "test",
                    this.requestMember.getId()));
        
        taskCategory2 =
            taskCategoryRepository.save(
                TaskCategory.of(
                    "test category 2",
                    TaskCategory.TaskCategoryCreationType.COMMON,
                    "test",
                    this.requestMember.getId()));
    }
    
    @Test
    @DisplayName("[SUCCESS] 투두모드 단일 Task 수정 - 루틴이 아닌 태스크 수정")
    void updateSingleTodoTaskTestContentOnly() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 6, 1, 0, 0));
        String originalTitle = "원래 타이틀";
        LocalDate originalDate = LocalDate.of(2024, 6, 2);
        LocalTime originalStartTime = LocalTime.of(9, 0);
        
        LocalDateTime originalDateTime =
            LocalDateTime.of(originalDate, originalStartTime);
        TodoTask todoTask =
            todoTaskRepository.save(
                TodoTask.of(
                    this.requestMember.getId(),
                    taskCategory.getId(),
                    originalTitle,
                    originalDate,
                    originalStartTime));
        
        // when
        String updatedTitle = "수정된 타이틀";
        LocalTime updatedStartTime = LocalTime.of(10, 0);
        LocalDateTime updateStartDateTime = LocalDateTime.of(
            originalDate.getYear(),
            originalDate.getMonth(),
            originalDate.getDayOfMonth(),
            updatedStartTime.getHour(),
            updatedStartTime.getMinute());
        Long updatedCategoryId = taskCategory2.getId();
        UpdateTodoTaskReqDto updateReq =
            new UpdateTodoTaskReqDto(updatedTitle, updateStartDateTime, updatedCategoryId, null);
        
        ResultActions resultActions =
            this.request(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(
                       URL + "/" + todoTask.getId())
                                                                                   .content(this.writeRequestBodyAsString(
                                                                                       updateReq)));
        
        // then
        resultActions.andExpect(status().isOk());
        
        // 조회 API를 통해 변경사항 검증
        MvcResult retrieveResult =
            this.request(
                    MockMvcRequestBuilders.get("/api/v1/tasks")
                                          .param("year", "2024")
                                          .param("month", "6"))
                .andExpect(status().isOk())
                .andReturn();
        
        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);
        
        tasks.todoTasks().stream()
             .filter(task -> task.id().equals(todoTask.getId()))
             .findFirst()
             .ifPresent(
                 task -> {
                     assertThat(task.title())
                         .isEqualTo(updatedTitle);
                     assertThat(task.startTime())
                         .isEqualTo(updatedStartTime);
                     assertThat(task.taskCategoryId())
                         .isEqualTo(updatedCategoryId);
                 });
    }
    
    @Test
    @DisplayName("[SUCCESS] 투두모드 단일 Task 수정 - 루틴으로 변경")
    void updateSingleTodoTaskTestConvertToRoutine() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 6, 1, 0, 0));
        String originalTitle = "원래 타이틀";
        LocalDate originalDate = LocalDate.of(2024, 6, 2);
        LocalTime originalStartTime = LocalTime.of(9, 0);
        
        TodoTask todoTask =
            todoTaskRepository.save(
                TodoTask.of(
                    this.requestMember.getId(),
                    taskCategory.getId(),
                    originalTitle,
                    originalDate,
                    originalStartTime));
        
        // when
        String updatedTitle = "수정된 타이틀";
        LocalTime updatedStartTime = LocalTime.of(10, 0);
        LocalDateTime updateStartDateTime = LocalDateTime.of(
            originalDate.getYear(),
            originalDate.getMonth(),
            originalDate.getDayOfMonth(),
            updatedStartTime.getHour(),
            updatedStartTime.getMinute());
        Long updatedCategoryId = taskCategory2.getId();
        LocalDate newRoutineEndDate = LocalDate.of(2024, 6, 30);
        UpdateTodoTaskReqDto updateReq =
            new UpdateTodoTaskReqDto(
                updatedTitle,
                updateStartDateTime,
                updatedCategoryId,
                UpdateTodoTaskRoutineReqDto.of(
                    originalDate, newRoutineEndDate, TodoTaskRoutineCycle.DAILY, null, false));
        
        long gap = ChronoUnit.DAYS.between(originalDate, newRoutineEndDate);
        
        ResultActions resultActions =
            this.request(
                MockMvcRequestBuilders.put(URL + "/" + todoTask.getId())
                                      .content(this.writeRequestBodyAsString(updateReq)));
        
        // then
        resultActions.andExpect(status().isOk());
        
        // 조회 API를 통해 변경사항 검증
        MvcResult retrieveResult =
            this.request(
                    MockMvcRequestBuilders.get("/api/v1/tasks")
                                          .param("year", "2024")
                                          .param("month", "6"))
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
        setFixedClock(LocalDateTime.of(2024, 6, 1, 0, 0));
        String originalTitle = "원래 타이틀";
        LocalDate startDate = LocalDate.of(2024, 6, 2);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        LocalTime startTime = LocalTime.of(9, 0);
        
        Set<LocalDate> routineDates = todoTaskRoutineDateCalculator.computeRoutineDates(
            TodoTaskRoutineCycle.DAILY,
            startDate,
            endDate,
            null
        );
        
        List<TodoTask> todoTasks = TodoTask.ofWithRoutine(
            this.requestMember.getId(),
            taskCategory.getId(),
            originalTitle,
            startTime,
            routineDates,
            TodoTaskRoutineCycle.DAILY,
            null,
            false
        );
        
        List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(todoTasks);
        
        // when
        String updatedTitle = "수정된 타이틀";
        LocalTime updatedStartTime = LocalTime.of(10, 0);
        LocalDateTime updateStartDateTime = LocalDateTime.of(
            startDate.getYear(),
            startDate.getMonth(),
            startDate.getDayOfMonth(),
            updatedStartTime.getHour(),
            updatedStartTime.getMinute());
        Long updatedCategoryId = taskCategory2.getId();
        
        TodoTask taskToModified = savedTodoTasks.stream()
                                                .filter(task -> task.getDate()
                                                                    .isEqual(LocalDate.of(2024,
                                                                                          6,
                                                                                          15)))
                                                .findFirst()
                                                .get();
        
        UpdateTodoTaskReqDto req =
            new UpdateTodoTaskReqDto(updatedTitle, updateStartDateTime, updatedCategoryId, null);
        
        ResultActions resultActions = this.request(
            MockMvcRequestBuilders.put(URL + "/" + taskToModified.getId())
                                  .content(this.writeRequestBodyAsString(req)));
        
        // then
        resultActions.andExpect(status().isOk());
        
        MvcResult retrieveResult =
            this.request(
                    MockMvcRequestBuilders.get("/api/v1/tasks")
                                          .param("year", "2024")
                                          .param("month", "6"))
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
                          .ifPresent(
                              task -> {
                                  assertThat(task.title()).isEqualTo(updatedTitle);
                                  assertThat(task.startTime()).isEqualTo(updatedStartTime);
                                  assertThat(task.taskCategoryId()).isEqualTo(taskCategory2.getId());
                              });
        
        assertThat(
            retrievedTodoTasks.stream()
                              .filter(task -> !task.id().equals(taskToModified.getId()))
                              .allMatch(
                                  task -> task.title().equals(originalTitle)
                                      && task.startTime().equals(startTime)
                                      && task.taskCategoryId()
                                             .equals(taskCategory.getId())))
            .isTrue();
    }
    
    @Test
    @DisplayName("[SUCCESS] 투두모드 루틴 컨텐츠 수정 - 모두 적용")
    void updateTodoTaskRoutineContentTestApplyAllRoutineTasks() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 6, 1, 0, 0));
        String originalTitle = "원래 타이틀";
        LocalDate startDate = LocalDate.of(2024, 6, 2);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        LocalTime startTime = LocalTime.of(9, 0);
        
        Set<LocalDate> routineDates = todoTaskRoutineDateCalculator.computeRoutineDates(
            TodoTaskRoutineCycle.DAILY,
            startDate,
            endDate,
            null
        );
        
        List<TodoTask> todoTasks = TodoTask.ofWithRoutine(
            this.requestMember.getId(),
            taskCategory.getId(),
            originalTitle,
            startTime,
            routineDates,
            TodoTaskRoutineCycle.DAILY,
            null,
            false
        );
        
        List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(todoTasks);
        
        // when
        String updatedTitle = "수정된 타이틀";
        LocalTime updatedStartTime = LocalTime.of(10, 0);
        LocalDateTime updatedStartDateTime = LocalDateTime.of(
            startDate.getYear(),
            startDate.getMonth(),
            startDate.getDayOfMonth(),
            updatedStartTime.getHour(),
            updatedStartTime.getMinute());
        
        // 모두 적용에서 의미는 없지만 ID가 필요하므로 Task를 가져옴
        TodoTask taskToModified = savedTodoTasks.stream()
                                                .filter(task -> task.getDate()
                                                                    .isEqual(LocalDate.of(2024,
                                                                                          6,
                                                                                          15)))
                                                .findFirst()
                                                .get();
        
        UpdateTodoTaskWithRoutineReqDto req =
            UpdateTodoTaskWithRoutineReqDto.builder()
                                           .title(updatedTitle)
                                           .startDateTime(updatedStartDateTime)
                                           .taskCategoryId(taskCategory2.getId())
                                           .build();
        
        ResultActions resultActions = this.request(
            MockMvcRequestBuilders.put(URL + "/" + taskToModified.getId() + "/with-routine")
                                  .content(this.writeRequestBodyAsString(req)));
        
        // then
        resultActions.andExpect(status().isOk());
        
        MvcResult retrieveResult =
            this.request(
                    MockMvcRequestBuilders.get("/api/v1/tasks")
                                          .param("year", "2024")
                                          .param("month", "6"))
                .andExpect(status().isOk())
                .andReturn();
        
        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);
        
        List<TodoTaskDto> retrievedTodoTasks = tasks.todoTasks();
        
        // 기준 일자 이후는 모두 수정되어야 함
        retrievedTodoTasks.stream()
                          .filter(task ->
                                      task.date().isAfter(taskToModified.getDate())
                                          || task.date().isEqual(taskToModified.getDate()))
                          .forEach(
                              task -> {
                                  assertThat(task.title()).isEqualTo(updatedTitle);
                                  assertThat(task.startTime()).isEqualTo(updatedStartTime);
                                  assertThat(task.taskCategoryId()).isEqualTo(taskCategory2.getId());
                              });
        
        // 기준 일자 이전은 변경 없음
        retrievedTodoTasks.stream()
                          .filter(task -> task.date().isBefore(taskToModified.getDate()))
                          .forEach(
                              task -> {
                                  assertThat(task.title()).isEqualTo(originalTitle);
                                  assertThat(task.startTime()).isEqualTo(startTime);
                                  assertThat(task.taskCategoryId()).isEqualTo(taskCategory.getId());
                              });
    }
    
    @Test
    @DisplayName("[SUCCESS] 투두모드 루틴 조건 수정")
    void updateTodoTaskRoutineConditionTest() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 6, 1, 0, 0));
        String title = "원래 타이틀";
        LocalDate startDate = LocalDate.of(2024, 6, 2);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        LocalTime startTime = LocalTime.of(9, 0);
        
        Set<LocalDate> routineDates = todoTaskRoutineDateCalculator.computeRoutineDates(
            TodoTaskRoutineCycle.DAILY,
            startDate,
            endDate,
            null
        );
        
        List<TodoTask> todoTasks = TodoTask.ofWithRoutine(
            this.requestMember.getId(),
            taskCategory.getId(),
            title,
            startTime,
            routineDates,
            TodoTaskRoutineCycle.DAILY,
            null,
            false
        );
        
        List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(todoTasks);
        
        // when
        Set<Integer> updatedPattern = Set.of(20, 21, 22);// 20, 21, 22일로 변경
        Integer updatedRoutineSize = updatedPattern.size();
        
        // ID가 필요하므로 Task를 가져옴
        TodoTask sample = savedTodoTasks.stream()
                                        .filter(task -> task.getDate()
                                                            .isEqual(LocalDate.of(2024,
                                                                                  6,
                                                                                  15)))
                                        .findFirst()
                                        .get();
        
        UpdateTodoTaskRoutineReqDto req =
            UpdateTodoTaskRoutineReqDto.of(
                LocalDate.of(2024, 6, 3),
                LocalDate.of(2024, 6, 30),
                TodoTaskRoutineCycle.MONTHLY,
                updatedPattern, // 10, 20, 30일로 변경
                false // 공휴일 제외 여부는 false로 유지
            );
        
        ResultActions resultActions = this.request(
            MockMvcRequestBuilders.put(URL + "/" + sample.getId() + "/routine")
                                  .content(this.writeRequestBodyAsString(req)));
        
        // then
        resultActions.andExpect(status().isOk());
        
        MvcResult retrieveResult =
            this.request(
                    MockMvcRequestBuilders.get("/api/v1/tasks")
                                          .param("year", "2024")
                                          .param("month", "6"))
                .andExpect(status().isOk())
                .andReturn();
        
        MockHttpServletResponse response = retrieveResult.getResponse();
        response.setCharacterEncoding("UTF-8");
        String content = response.getContentAsString();
        RetrieveTasksResDto tasks = this.readResponse(content, RetrieveTasksResDto.class);
        
        List<TodoTaskDto> retrievedTodoTasks = tasks.todoTasks();
        
        retrievedTodoTasks
            .stream()
            .filter(
                task -> task.date().isBefore(sample.getDate()))
            .forEach(
                task -> {
                    assertThat(task.title()).isEqualTo(title);
                    assertThat(task.startTime()).isEqualTo(startTime);
                    assertThat(task.taskCategoryId()).isEqualTo(taskCategory.getId());
                });
        
        assertThat(retrievedTodoTasks
                       .stream()
                       .filter(
                           task -> task.date().isEqual(sample.getDate())
                               || task.date().isAfter(sample.getDate()))
                       .count())
            .isEqualTo(3 + 1);
    }
}