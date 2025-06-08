package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TodoTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.*;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RetrieveTaskIntegrationTest extends AbstractIntegrationTest {

    static final String RETRIEVE_TASKS_URL = "/api/v1/tasks";

    @Autowired DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired TodoTaskJpaRepository todoTaskJpaRepository;

    @Autowired TaskCategoryJpaRepository taskCategoryJpaRepository;

    private TaskCategory taskCategory1, taskCategory2;
    private TodoTask todoTask1, todoTask2;
    private DowithTask dowithTask1, dowithTask2, dowithTask3;

    protected void deleteTestData() {
        todoTaskJpaRepository.deleteAll();
        dowithTaskJpaRepository.deleteAll();
        taskCategoryJpaRepository.deleteAll();
    }

    protected void createTestData() {

        SystemTimeUtil.setClock(
                Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC));

        taskCategory1 =
                taskCategoryJpaRepository.save(
                        TaskCategory.of(
                                "test1",
                                TaskCategory.TaskCategoryCreationType.COMMON,
                                "test1-emoji",
                                this.requestMember.getId()));
        taskCategory2 =
                taskCategoryJpaRepository.save(
                        TaskCategory.of(
                                "test2",
                                TaskCategory.TaskCategoryCreationType.COMMON,
                                "test2-emoji",
                                this.requestMember.getId()));
        taskCategoryJpaRepository.saveAll(List.of(taskCategory1, taskCategory2));

        // 1. TodoTask 2개
        todoTask1 =
                TodoTask.of(
                        this.requestMember.getId(),
                        taskCategory1.getId(),
                        "test todo task 1",
                        LocalDate.of(2024, 3, 10),
                        LocalTime.of(10, 0));
        todoTask2 =
                TodoTask.of(
                        this.requestMember.getId(),
                        taskCategory2.getId(),
                        "test todo task 2",
                        LocalDate.of(2024, 3, 11),
                        LocalTime.of(11, 0));
        todoTaskJpaRepository.saveAll(List.of(todoTask1, todoTask2));

        // 2. DowithTask 2개
        dowithTask1 =
                DowithTask.of(
                        this.requestMember.getId(),
                        taskCategory1.getId(),
                        "test dowith task 1",
                        LocalDate.of(2024, 3, 12),
                        LocalTime.of(12, 0));
        dowithTask2 =
                DowithTask.of(
                        this.requestMember.getId(),
                        taskCategory2.getId(),
                        "test dowith task 2",
                        LocalDate.of(2024, 3, 13),
                        LocalTime.of(13, 0));

        dowithTask3 =
                DowithTask.of(
                        this.requestMember.getId(),
                        taskCategory1.getId(),
                        "test dowith task 3",
                        LocalDate.of(2024, 3, 14),
                        LocalTime.of(14, 0));

        dowithTask3.confirm(
                List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg"));

        dowithTaskJpaRepository.saveAll(List.of(dowithTask1, dowithTask2, dowithTask3));
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
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todoTasks[0].id").value(todoTask1.getId()))
                .andExpect(jsonPath("$.data.todoTasks[0].taskCategoryId").value(taskCategory1.getId()))
                .andExpect(jsonPath("$.data.todoTasks[0].taskCategoryName").value(taskCategory1.getTitle()))
                .andExpect(jsonPath("$.data.todoTasks[0].title").value(todoTask1.getTitle()))
                .andExpect(jsonPath("$.data.todoTasks[0].status").value(todoTask1.getStatus().name()))
                .andExpect(jsonPath("$.data.todoTasks[0].date").value(todoTask1.getDate().toString()))
                .andExpect(
                        jsonPath("$.data.todoTasks[0].startTime")
                                .value(DateTimeUtil.toFormatString(todoTask1.getStartTime())))
                .andExpect(jsonPath("$.data.todoTasks[1].id").value(todoTask2.getId()))
                .andExpect(jsonPath("$.data.todoTasks[1].taskCategoryId").value(taskCategory2.getId()))
                .andExpect(jsonPath("$.data.todoTasks[1].taskCategoryName").value(taskCategory2.getTitle()))
                .andExpect(jsonPath("$.data.todoTasks[1].title").value(todoTask2.getTitle()))
                .andExpect(jsonPath("$.data.todoTasks[1].status").value(todoTask2.getStatus().name()))
                .andExpect(jsonPath("$.data.todoTasks[1].date").value(todoTask2.getDate().toString()))
                .andExpect(
                        jsonPath("$.data.todoTasks[1].startTime")
                                .value(DateTimeUtil.toFormatString(todoTask2.getStartTime())))
                .andExpect(jsonPath("$.data.dowithTasks[0].id").value(dowithTask1.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[0].taskCategoryId").value(taskCategory1.getId()))
                .andExpect(
                        jsonPath("$.data.dowithTasks[0].taskCategoryName").value(taskCategory1.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[0].title").value(dowithTask1.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[0].status").value(dowithTask1.getStatus().name()))
                .andExpect(jsonPath("$.data.dowithTasks[0].date").value(dowithTask1.getDate().toString()))
                .andExpect(
                        jsonPath("$.data.dowithTasks[0].startTime")
                                .value(DateTimeUtil.toFormatString(dowithTask1.getStartTime())))
                .andExpect(jsonPath("$.data.dowithTasks[0].confirmedImageUrls").isEmpty())
                .andExpect(jsonPath("$.data.dowithTasks[1].id").value(dowithTask2.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[1].taskCategoryId").value(taskCategory2.getId()))
                .andExpect(
                        jsonPath("$.data.dowithTasks[1].taskCategoryName").value(taskCategory2.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[1].title").value(dowithTask2.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[1].status").value(dowithTask2.getStatus().name()))
                .andExpect(jsonPath("$.data.dowithTasks[1].date").value(dowithTask2.getDate().toString()))
                .andExpect(
                        jsonPath("$.data.dowithTasks[1].startTime")
                                .value(DateTimeUtil.toFormatString(dowithTask2.getStartTime())))
                .andExpect(jsonPath("$.data.dowithTasks[1].confirmedImageUrls").isEmpty())
                .andExpect(jsonPath("$.data.dowithTasks[2].id").value(dowithTask3.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[2].taskCategoryId").value(taskCategory1.getId()))
                .andExpect(
                        jsonPath("$.data.dowithTasks[2].taskCategoryName").value(taskCategory1.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[2].title").value(dowithTask3.getTitle()))
                .andExpect(jsonPath("$.data.dowithTasks[2].status").value(dowithTask3.getStatus().name()))
                .andExpect(jsonPath("$.data.dowithTasks[2].date").value(dowithTask3.getDate().toString()))
                .andExpect(
                        jsonPath("$.data.dowithTasks[2].startTime")
                                .value(DateTimeUtil.toFormatString(dowithTask3.getStartTime())))
                .andExpect(jsonPath("$.data.dowithTasks[2].confirmedImageUrls").isArray())
                .andExpect(
                        jsonPath("$.data.dowithTasks[2].confirmedImageUrls")
                                .value(
                                        Matchers.is(
                                                List.of(
                                                        "https://example.com/image1.jpg", "https://example.com/image2.jpg"))));
    }
}
