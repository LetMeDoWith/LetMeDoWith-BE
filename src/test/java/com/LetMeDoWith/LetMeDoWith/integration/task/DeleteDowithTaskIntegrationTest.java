package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
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
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class DeleteDowithTaskIntegrationTest extends AbstractIntegrationTest {

    static final String DELETE_TASK_URL = "/api/v1/tasks/dowith" + "/{dowithTaskId}";

    @Autowired DowithTaskJpaRepository dowithTaskJpaRepository;
    @Autowired TaskCategoryJpaRepository taskCategoryJpaRepository;

    private TaskCategory taskCategory;

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();
        taskCategoryJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        taskCategory =
                taskCategoryJpaRepository.save(
                        TaskCategory.of(
                                "test",
                                TaskCategory.TaskCategoryCreationType.COMMON,
                                "test",
                                this.requestMember.getId()));
    }

    @Test
    @DisplayName("[SUCCESS] Routine이 없는 Task 삭제")
    void deleteDowithTask1() throws Exception {

        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask =
                dowithTaskJpaRepository.save(
                        DowithTask.of(
                                this.requestMember.getId(),
                                taskCategory.getId(),
                                "test",
                                SystemTimeUtil.nowDate().plusDays(1),
                                // 시작시간 :  현재 시간 기준 다음날
                                LocalTime.of(1, 0)));

        // when
        ResultActions resultActions =
                request(
                        MockMvcRequestBuilders.delete(DELETE_TASK_URL, dowithTask.getId())
                                .param("isRoutineInclude", String.valueOf(false)));
        // then
        resultActions.andExpect(status().isOk());
        assertThat(dowithTaskJpaRepository.findById(dowithTask.getId())).isEmpty();
    }

    @Test
    @DisplayName("[FAIL] Routine이 없는 Task 삭제 - 시작시간이 과거인 경우")
    void deleteDowithTask2() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask =
                dowithTaskJpaRepository.save(
                        DowithTask.of(
                                this.requestMember.getId(),
                                taskCategory.getId(),
                                "test",
                                SystemTimeUtil.now().plusDays(1).toLocalDate(),
                                // 시작시간 :  과거
                                LocalTime.of(1, 0)));

        // when
        setFixedClock(LocalDateTime.of(2024, 3, 15, 0, 0));
        ResultActions resultActions =
                request(
                        MockMvcRequestBuilders.delete(DELETE_TASK_URL, dowithTask.getId())
                                .param("isRoutineInclude", String.valueOf(false)));

        // then
        resultActions.andExpect(status().is4xxClientError());
        assertThat(dowithTaskJpaRepository.findById(dowithTask.getId())).isPresent();
    }

    @Test
    @DisplayName("[SUCCESS] Routine이 있는 Task 삭제")
    void deleteDowithTaskWithRoutine() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        Set<LocalDate> routineDateSet = new HashSet<>();
        routineDateSet.add(LocalDate.of(2024, 3, 5)); // 삭제되지 않아야 할 Routine
        routineDateSet.add(LocalDate.of(2024, 3, 7)); // 삭제되지 않아야 할 Routine
        routineDateSet.add(LocalDate.of(2024, 3, 16)); // 삭제되어야 할 Routine
        routineDateSet.add(LocalDate.of(2024, 3, 18)); // 삭제되어야 할 Routine
        List<DowithTask> dowithTasks =
                dowithTaskJpaRepository.saveAll(
                        DowithTask.ofWithRoutine(
                                this.requestMember.getId(),
                                taskCategory.getId(),
                                "test",
                                LocalDate.of(2024, 3, 15),
                                LocalTime.of(1, 0),
                                routineDateSet));

        Long targetDowithTaskID =
                dowithTasks.stream()
                        .filter(task -> task.getDate().equals(LocalDate.of(2024, 3, 15)))
                        .toList()
                        .get(0)
                        .getId();

        List<DowithTask> toSurviveTasks =
                dowithTasks.stream()
                        .filter(task -> task.getDate().isBefore(LocalDate.of(2024, 3, 15)))
                        .toList();

        List<DowithTask> toDeleteTasks =
                dowithTasks.stream()
                        .filter(task -> task.getDate().isAfter(LocalDate.of(2024, 3, 15)))
                        .toList();

        // when
        setFixedClock(LocalDateTime.of(2024, 3, 15, 0, 0));
        ResultActions resultActions =
                request(
                        MockMvcRequestBuilders.delete(DELETE_TASK_URL, targetDowithTaskID)
                                .param("isRoutineInclude", String.valueOf(true)));

        // then
        resultActions.andExpect(status().isOk());

        toSurviveTasks.forEach(
                task -> assertThat(dowithTaskJpaRepository.findById(task.getId())).isPresent());
        toSurviveTasks.forEach(
                task ->
                        assertThat(
                                        dowithTaskJpaRepository
                                                .findById(task.getId())
                                                .get()
                                                .getRoutine()
                                                .getRoutineDates()
                                                .getDates())
                                .isEqualTo(
                                        Set.of(
                                                LocalDate.of(2024, 3, 5),
                                                LocalDate.of(2024, 3, 7),
                                                LocalDate.of(2024, 3, 15))));

        toDeleteTasks.forEach(
                task -> assertThat(dowithTaskJpaRepository.findById(task.getId())).isEmpty());
    }
}
