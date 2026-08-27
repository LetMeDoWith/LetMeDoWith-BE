package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TodoTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTasksResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTasksResDto.TodoTaskDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTodoTaskResDto;
import java.io.UnsupportedEncodingException;
import java.time.*;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;

public class RetrieveTaskIntegrationTest extends AbstractIntegrationTest {

    static final String RETRIEVE_TASKS_URL = "/api/v1/tasks";
    static final String RETRIEVE_SINGLE_TODO_URL = "/api/v1/tasks/todo";
    static final String RETRIEVE_SINGLE_DOWITH_URL = "/api/v1/tasks/dowith";

    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    TodoTaskJpaRepository todoTaskJpaRepository;

    @Autowired
    TodoTaskRoutineRepository todoTaskRoutineRepository;

    @Autowired
    TaskCategoryJpaRepository taskCategoryJpaRepository;

    @Autowired
    DowithTaskFeedbackJpaRepository dowithTaskFeedbackJpaRepository;

    private TaskCategory taskCategory1, taskCategory2;
    private TodoTask todoTask1, todoTask2, todoTask3;
    private DowithTask dowithTask1, dowithTask2, dowithTask3;

    protected void deleteTestData() {
        dowithTaskFeedbackJpaRepository.deleteAll();
        todoTaskJpaRepository.deleteAll();
        dowithTaskJpaRepository.deleteAll();
        taskCategoryJpaRepository.deleteAll();
    }

    protected void createTestData() {

        SystemTimeUtil.setClock(
                Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC));

        taskCategory1 = taskCategoryJpaRepository.save(TaskCategory.of(
                "test1", TaskCategory.TaskCategoryCreationType.COMMON, "test1-emoji", this.requestMember.getId()));
        taskCategory2 = taskCategoryJpaRepository.save(TaskCategory.of(
                "test2", TaskCategory.TaskCategoryCreationType.COMMON, "test2-emoji", this.requestMember.getId()));
        taskCategoryJpaRepository.saveAll(List.of(taskCategory1, taskCategory2));

        // 1. TodoTask 2개
        todoTask1 = TodoTask.of(
                this.requestMember.getId(),
                taskCategory1.getId(),
                "test todo task 1",
                LocalDate.of(2024, 3, 10),
                LocalTime.of(10, 0));
        todoTask2 = TodoTask.of(
                this.requestMember.getId(),
                taskCategory2.getId(),
                "test todo task 2",
                LocalDate.of(2024, 3, 11),
                LocalTime.of(11, 0));

        todoTask3 = TodoTask.ofWithRoutine(
                this.requestMember.getId(),
                taskCategory1.getId(),
                "test todo task 1",
                LocalDate.of(2024, 3, 10),
                LocalTime.of(10, 0),
                LocalDate.of(2024, 3, 10),
                LocalDate.of(2024, 3, 10),
                TaskRoutineCycle.DAILY,
                null,
                false);

        todoTaskJpaRepository.saveAll(List.of(todoTask1, todoTask2, todoTask3));

        // 2. DowithTask 2개
        dowithTask1 = DowithTask.of(
                this.requestMember.getId(),
                taskCategory1.getId(),
                "test dowith task 1",
                LocalDate.of(2024, 3, 12),
                LocalTime.of(12, 0),
                LocalDate.of(2024, 3, 12),
                LocalDate.of(2024, 3, 20),
                TaskRoutineCycle.DAILY,
                null,
                false);
        dowithTask2 = DowithTask.of(
                this.requestMember.getId(),
                taskCategory2.getId(),
                "test dowith task 2",
                LocalDate.of(2024, 3, 13),
                LocalTime.of(13, 0));

        dowithTask3 = DowithTask.of(
                this.requestMember.getId(),
                taskCategory1.getId(),
                "test dowith task 3",
                LocalDate.of(2024, 3, 14),
                LocalTime.of(14, 0));

        dowithTask3.success(List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg"));

        dowithTaskJpaRepository.saveAll(List.of(dowithTask1, dowithTask2, dowithTask3));

        // 3. DowithTaskFeedback - dowithTask1: 2개, dowithTask2: 1개, dowithTask3: 0개
        String feedbackSenderId = "feedback-sender-member";
        dowithTaskFeedbackJpaRepository.saveAll(List.of(
                DowithTaskFeedback.of(feedbackSenderId, this.requestMember.getId(), dowithTask1.getId(), 1L),
                DowithTaskFeedback.of(feedbackSenderId, this.requestMember.getId(), dowithTask1.getId(), 1L),
                DowithTaskFeedback.of(feedbackSenderId, this.requestMember.getId(), dowithTask2.getId(), 1L)));
    }

    @Test
    @DisplayName("[SUCCESS] Task 목록 조회")
    void retrieveTasks1() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        // when
        var result = request(get(RETRIEVE_TASKS_URL).param("year", "2024").param("month", "3"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todoTasks[0].id").value(todoTask1.getId()))
                .andExpect(jsonPath("$.data.todoTasks[0].taskCategoryId").value(taskCategory1.getId()))
                .andExpect(jsonPath("$.data.todoTasks[0].taskCategoryName").value(taskCategory1.getTitle()))
                .andExpect(jsonPath("$.data.todoTasks[0].title").value(todoTask1.getTitle()))
                .andExpect(jsonPath("$.data.todoTasks[0].status")
                        .value(todoTask1.getStatus().name()))
                .andExpect(jsonPath("$.data.todoTasks[0].date")
                        .value(todoTask1.getDate().toString()))
                .andExpect(jsonPath("$.data.todoTasks[0].startTime")
                        .value(DateTimeUtil.toFormatString(todoTask1.getStartTime())))
                .andExpect(jsonPath("$.data.todoTasks[0].isRoutine").value(false))
                // TODO - 추후 Dowith Routine 추가 후 검증
                // 필요
                .andExpect(jsonPath("$.data.todoTasks[0].startTime")
                        .value(DateTimeUtil.toFormatString(todoTask1.getStartTime())))
                .andExpect(jsonPath("$.data.todoTasks[1].id").value(todoTask2.getId()))
                .andExpect(jsonPath("$.data.todoTasks[1].taskCategoryId").value(taskCategory2.getId()))
                .andExpect(jsonPath("$.data.todoTasks[1].taskCategoryName").value(taskCategory2.getTitle()))
                .andExpect(jsonPath("$.data.todoTasks[1].title").value(todoTask2.getTitle()))
                .andExpect(jsonPath("$.data.todoTasks[1].status")
                        .value(todoTask2.getStatus().name()))
                .andExpect(jsonPath("$.data.todoTasks[1].date")
                        .value(todoTask2.getDate().toString()))
                .andExpect(jsonPath("$.data.todoTasks[1].startTime")
                        .value(DateTimeUtil.toFormatString(todoTask2.getStartTime())))
                .andExpect(jsonPath("$.data.todoTasks[1].isRoutine").value(false))
                // TODO - 추후 Dowith Routine 추가 후 검증
                // 필요
                .andExpect(jsonPath("$.data.dowithTasks[0].id").value(dowithTask1.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[0].taskCategoryId").value(taskCategory1.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[0].taskCategoryName").value(taskCategory1.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[0].title").value(dowithTask1.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[0].status")
                        .value(dowithTask1.getStatus().name()))
                .andExpect(jsonPath("$.data.dowithTasks[0].date")
                        .value(dowithTask1.getDate().toString()))
                .andExpect(jsonPath("$.data.dowithTasks[0].startTime")
                        .value(DateTimeUtil.toFormatString(dowithTask1.getStartTime())))
                .andExpect(jsonPath("$.data.dowithTasks[0].successImageUrls").doesNotExist())
                .andExpect(jsonPath("$.data.dowithTasks[0].isRoutine").value(true))
                .andExpect(jsonPath("$.data.dowithTasks[0].feedBackCount").value(2))
                .andExpect(jsonPath("$.data.dowithTasks[1].id").value(dowithTask2.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[1].taskCategoryId").value(taskCategory2.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[1].taskCategoryName").value(taskCategory2.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[1].title").value(dowithTask2.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[1].status")
                        .value(dowithTask2.getStatus().name()))
                .andExpect(jsonPath("$.data.dowithTasks[1].date")
                        .value(dowithTask2.getDate().toString()))
                .andExpect(jsonPath("$.data.dowithTasks[1].startTime")
                        .value(DateTimeUtil.toFormatString(dowithTask2.getStartTime())))
                .andExpect(jsonPath("$.data.dowithTasks[1].successImageUrls").doesNotExist())
                .andExpect(jsonPath("$.data.dowithTasks[1].isRoutine").value(false))
                .andExpect(jsonPath("$.data.dowithTasks[1].feedBackCount").value(1))
                .andExpect(jsonPath("$.data.dowithTasks[2].id").value(dowithTask3.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[2].taskCategoryId").value(taskCategory1.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[2].taskCategoryName").value(taskCategory1.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[2].title").value(dowithTask3.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[2].status")
                        .value(dowithTask3.getStatus().name()))
                .andExpect(jsonPath("$.data.dowithTasks[2].date")
                        .value(dowithTask3.getDate().toString()))
                .andExpect(jsonPath("$.data.dowithTasks[2].startTime")
                        .value(DateTimeUtil.toFormatString(dowithTask3.getStartTime())))
                .andExpect(jsonPath("$.data.dowithTasks[2].successImageUrls").isArray())
                .andExpect(jsonPath("$.data.dowithTasks[2].successImageUrls")
                        .value(Matchers.is(
                                List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg"))))
                .andExpect(jsonPath("$.data.dowithTasks[2].isRoutine").value(false))
                .andExpect(jsonPath("$.data.dowithTasks[2].feedBackCount").value(0));
    }

    @Test
    @DisplayName("[SUCCESS] DowithTask 단일조회")
    void retrieveDowithTakTest() throws Exception {
        // given
        Long dowithTaskId = dowithTask1.getId();

        // when
        ResultActions result = this.request(get(RETRIEVE_SINGLE_DOWITH_URL + "/" + dowithTaskId));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(dowithTask1.getId()))
                .andExpect(jsonPath("$.data.taskCategoryId").value(taskCategory1.getId()))
                .andExpect(jsonPath("$.data.taskCategoryName").value(taskCategory1.getTitle()))
                .andExpect(jsonPath("$.data.title").value(dowithTask1.getTitle()))
                .andExpect(jsonPath("$.data.status").value(dowithTask1.getStatus().code))
                .andExpect(jsonPath("$.data.date").value(dowithTask1.getDate().toString()))
                .andExpect(jsonPath("$.data.startTime").value(DateTimeUtil.toFormatString(dowithTask1.getStartTime())))
                .andExpect(jsonPath("$.data.successImageUrls").doesNotExist())
                .andExpect(jsonPath("$.data.routineCondition").exists())
                .andExpect(jsonPath("$.data.routineCondition.startDate")
                        .value(dowithTask1.getRoutine().getRangeStartDate().toString()))
                .andExpect(jsonPath("$.data.routineCondition.endDate")
                        .value(dowithTask1.getRoutine().getRangeEndDate().toString()))
                .andExpect(jsonPath("$.data.routineCondition.cycle")
                        .value(dowithTask1.getRoutine().getCycle().getCode()))
                .andExpect(jsonPath("$.data.routineCondition.pattern").doesNotExist())
                .andExpect(jsonPath("$.data.routineCondition.isExcludeHolidays").value(false))
                .andExpect(jsonPath("$.data.feedBackCount").value(2));
    }

    @Test
    @DisplayName("[SUCCESS] TodoTask 단일조회")
    void retrieveTodoTaskTest() throws UnsupportedEncodingException {
        // given
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        MockHttpServletResponse response = request(
                        get(RETRIEVE_TASKS_URL).param("year", "2024").param("month", "3"))
                .andReturn()
                .getResponse();

        response.setCharacterEncoding("UTF-8");

        String contentAsString = response.getContentAsString();

        RetrieveTasksResDto retrieveTasksResDto = this.readResponse(contentAsString, RetrieveTasksResDto.class);

        TodoTaskDto todoTaskDto = retrieveTasksResDto.todoTasks().stream()
                .filter(task -> task.isRoutine().equals(true))
                .findFirst()
                .get();

        // when
        MockHttpServletResponse singleRetrieveResponse = request(
                        get(RETRIEVE_SINGLE_TODO_URL + "/" + todoTaskDto.id().toString()))
                .andReturn()
                .getResponse();
        singleRetrieveResponse.setCharacterEncoding("UTF-8");

        RetrieveTodoTaskResDto retrieveTodoTaskResDto =
                this.readResponse(singleRetrieveResponse.getContentAsString(), RetrieveTodoTaskResDto.class);

        // then
        Assertions.assertEquals(retrieveTodoTaskResDto.id(), todoTaskDto.id());
        Assertions.assertEquals(retrieveTodoTaskResDto.title(), todoTaskDto.title());
    }
}
