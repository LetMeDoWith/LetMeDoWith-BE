package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class DeleteDowithTaskIntegrationTest extends AbstractIntegrationTest {

    static final String DELETE_TASK_URL = "/api/v1/tasks/dowith" + "/{id}";
    static final String DELETE_TASK_WITH_ROUTINE_URL = "/api/v1/tasks/dowith" + "/{id}" + "/with-routine";
    static final String RETRIEVE_TASKS_URL = "/api/v1/tasks";

    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    TaskCategoryJpaRepository taskCategoryJpaRepository;

    private TaskCategory taskCategory;

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();
        taskCategoryJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        taskCategory = taskCategoryJpaRepository.save(TaskCategory.of(
                "test", TaskCategory.TaskCategoryCreationType.COMMON, "test", this.requestMember.getId()));
        this.taskSummary.plusRemainedDowithTaskCount(5);
        this.taskSummaryJpaRepository.saveAndFlush(this.taskSummary);
    }

    @Test
    @DisplayName("[SUCCESS] Routine이 없는 Task 삭제")
    void deleteDowithTask1() throws Exception {

        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "test",
                SystemTimeUtil.nowDate().plusDays(1),
                // 시작시간 : 현재 시간 기준 다음날
                LocalTime.of(1, 0)));

        // when
        ResultActions deleteResultActions =
                this.request(MockMvcRequestBuilders.delete(DELETE_TASK_URL, dowithTask.getId()));
        ResultActions retrieveResultActions = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk());

        // then
        deleteResultActions.andExpect(status().isOk());
        assertThat(dowithTaskJpaRepository.findById(dowithTask.getId())).isEmpty();
        retrieveResultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dowithTasks").isEmpty());
    }

    @Test
    @DisplayName("[FAIL] Routine이 없는 Task 삭제 - 시작시간이 과거인 경우")
    void deleteDowithTask2() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "test",
                SystemTimeUtil.now().plusDays(1).toLocalDate(),
                // 시작시간 : 과거
                LocalTime.of(1, 0)));

        // when
        setFixedClock(LocalDateTime.of(2024, 3, 15, 0, 0));
        ResultActions resultActions = request(MockMvcRequestBuilders.delete(DELETE_TASK_URL, dowithTask.getId()));
        ResultActions retrieveResultActions = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk());

        // then
        resultActions.andExpect(status().is4xxClientError());
        assertThat(dowithTaskJpaRepository.findById(dowithTask.getId())).isPresent();
        retrieveResultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dowithTasks[0].id").value(dowithTask.getId()))
                .andExpect(jsonPath("$.data.dowithTasks[0].date")
                        .value(dowithTask.getDate().toString()));
    }

    @Test
    @DisplayName("[SUCCESS] Routine이 있는 Task 삭제")
    void deleteDowithTaskWithRoutine() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        LocalDate date = LocalDate.of(2024, 3, 1);
        LocalTime startTime = LocalTime.of(13, 0, 0);
        int plusDays = 13;
        Set<LocalDate> routieDates = new HashSet<>();
        for (int i = 1; i <= plusDays; i++) {
            routieDates.add(date.plusDays(i));
        }
        DowithTask dowithTask = DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "테스트",
                date,
                startTime,
                date,
                date.plusDays(plusDays),
                TaskRoutineCycle.DAILY,
                null,
                false);
        List<DowithTask> routineDowithTasks = DowithTask.of(dowithTask, routieDates);
        List<DowithTask> dowithTasks =
                dowithTaskJpaRepository.saveAll(Stream.concat(Stream.of(dowithTask), routineDowithTasks.stream())
                        .toList());
        dowithTaskJpaRepository.flush();

        LocalDate standardDate = LocalDate.of(2024, 3, 10);
        Long targetDowithTaskID = dowithTasks.stream()
                .filter(task -> task.getDate().equals(standardDate))
                .map(DowithTask::getId)
                .findFirst()
                .orElseThrow();

        List<DowithTask> toSurviveTasks = dowithTasks.stream()
                .filter(task -> task.getDate().isBefore(standardDate))
                .toList();

        List<DowithTask> toDeleteTasks = dowithTasks.stream()
                .filter(task ->
                        task.getDate().isAfter(standardDate) || task.getDate().equals(standardDate))
                .toList();

        // when
        // 요청 시간 3월 15일로 고정
        setFixedClock(LocalDateTime.of(2024, 3, 10, 0, 0));
        ResultActions resultActions =
                request(MockMvcRequestBuilders.delete(DELETE_TASK_WITH_ROUTINE_URL, targetDowithTaskID));
        ResultActions retrieveResultActions = this.request(MockMvcRequestBuilders.get(RETRIEVE_TASKS_URL)
                        .param("year", "2024")
                        .param("month", "3"))
                .andExpect(status().isOk());

        // then
        resultActions.andExpect(status().isOk());
        toSurviveTasks.forEach(task -> {
            Optional<DowithTask> opTask = dowithTaskJpaRepository.findById(task.getId());
            assertThat(opTask).isPresent();
        });

        toDeleteTasks.forEach(task ->
                assertThat(dowithTaskJpaRepository.findById(task.getId())).isEmpty());
        //        retrieveResultActions
        //                .andExpect(status().isOk())
        //                .andExpect(jsonPath("$.data.dowithTasks[0].id")
        //                        .value(toSurviveTasks.get(0).getId()))
        //                .andExpect(jsonPath("$.data.dowithTasks[0].date")
        //                        .value(toSurviveTasks.get(0).getDate().toString()))
        //                .andExpect(jsonPath("$.data.dowithTasks[1].id")
        //                        .value(toSurviveTasks.get(1).getId()))
        //                .andExpect(jsonPath("$.data.dowithTasks[1].date")
        //                        .value(toSurviveTasks.get(1).getDate().toString()));
    }
}
