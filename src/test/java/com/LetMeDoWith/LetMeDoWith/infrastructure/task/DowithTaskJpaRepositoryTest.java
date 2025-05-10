package com.LetMeDoWith.LetMeDoWith.infrastructure.task;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.LetMeDoWith.LetMeDoWith.config.TestQueryDslConfig;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import jakarta.persistence.NoResultException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Slf4j
@Import(TestQueryDslConfig.class)
public class DowithTaskJpaRepositoryTest {

    // DowithTask 테스트 data
    private static final Long memberId = 1L;
    private static final Long taskCategoryId1 = 1L;
    private static final Long taskCategoryId2 = 2L;
    private static final String title1 = "아침 먹기";
    private static final String title2 = "점심 먹기";
    private static final LocalDate date1 = LocalDate.now();
    private static final LocalDate date2 = LocalDate.now().plusDays(1);
    private static final LocalTime startTime1 = LocalTime.now().plusMinutes(10);
    private static final LocalTime startTime2 = LocalTime.now().plusMinutes(20);
    private static final LocalDateTime successDateTime = null;
    // DowithTaskRoutine 테스트 데이터
    private static final LocalDate routineDate1_1 = date1.plusDays(2);
    private static final LocalDate routineDate1_2 = date1.plusDays(4);
    private static final LocalDate routineDate1_3 = date1.plusDays(6);
    private static final LocalDate routineDate2_1 = date2.plusDays(2);
    private static final LocalDate routineDate2_2 = date2.plusDays(4);
    private final LocalDateTime completeDateTime = null;
    @Autowired private TestEntityManager entityManager;
    @Autowired private DowithTaskJpaRepository dowithTaskJpaRepository;

    @BeforeEach
    void beforeEach() {
        entityManager.clear();
    }

    @Test
    @DisplayName("DowithTask Aggregate save 테스트")
    void test_dowithTask_save() {
        // given
        DowithTask dowithTask1 = DowithTask.of(memberId, taskCategoryId1, title1, date1, startTime1);
        DowithTask dowithTask2 = DowithTask.of(memberId, taskCategoryId2, title2, date2, startTime2);

        // when
        DowithTask savedDowithTask1 = dowithTaskJpaRepository.save(dowithTask1);
        DowithTask savedDowithTask2 = dowithTaskJpaRepository.save(dowithTask2);

        // then
        assertThat(savedDowithTask1.getId()).isNotNull();
        assertThat(savedDowithTask2.getId()).isNotNull();
    }

    @Test
    @DisplayName("DowithTask Aggregate save 테스트 (루틴 있는 경우)")
    void test_dowithTask_routine_save() {
        // given
        List<DowithTask> dowithTasks1 =
                DowithTask.ofWithRoutine(
                        memberId,
                        taskCategoryId1,
                        title1,
                        date1,
                        startTime1,
                        Set.of(routineDate1_1, routineDate1_2, routineDate1_3));
        List<DowithTask> dowithTasks2 =
                DowithTask.ofWithRoutine(
                        memberId,
                        taskCategoryId2,
                        title2,
                        date2,
                        startTime2,
                        Set.of(routineDate2_1, routineDate2_2));

        // when
        List<DowithTask> savedDowithTask1 = dowithTaskJpaRepository.saveAll(dowithTasks1);
        List<DowithTask> savedDowithTask2 = dowithTaskJpaRepository.saveAll(dowithTasks2);

        // then
        savedDowithTask1.forEach(
                task -> {
                    assertThat(task.getId()).isNotNull();
                    assertThat(task.getRoutine().getId()).isNotNull();
                    assertThat(task.getRoutine().getRoutineDates()).isNotNull();
                });
        savedDowithTask2.forEach(
                task -> {
                    assertThat(task.getId()).isNotNull();
                    assertThat(task.getRoutine().getId()).isNotNull();
                    assertThat(task.getRoutine().getRoutineDates()).isNotNull();
                });
    }

    @Test
    @DisplayName("DowithTask Agregate 조회")
    void test_findDowithTaskAggregate() {
        // given
        DowithTask dowithTask = DowithTask.of(memberId, taskCategoryId1, title1, date1, startTime1);

        // when
        DowithTask savedTask = dowithTaskJpaRepository.save(dowithTask);
        Long id = savedTask.getId();
        entityManager.flush();
        DowithTask target =
                dowithTaskJpaRepository
                        .findDowithTaskAggregate(id, memberId)
                        .orElseThrow(() -> new NoResultException());

        // then
        assertThat(target.getId()).isEqualTo(savedTask.getId());
        assertThat(target.getMemberId()).isEqualTo(memberId);
        assertThat(target.getTaskCategoryId()).isEqualTo(taskCategoryId1);
        assertThat(target.getTitle()).isEqualTo(title1);
        assertThat(target.getStatus()).isEqualTo(DowithTaskStatus.WAIT);
        assertThat(target.getDate()).isEqualTo(date1);
        assertThat(target.getStartTime()).isEqualTo(startTime1);
        assertThat(target.getSuccessDateTime()).isNull();
        assertThat(target.getCompleteDateTime()).isNull();
        assertThat(target.getConfirms()).isNull();
        assertThat(target.getRoutine()).isNull();
    }

    @Test
    @DisplayName("DowithTask Aggregate 조회 (루틴 있는 경우)")
    void test_findDowithTaskAggregate2() {
        // given
        List<DowithTask> dowithTasksWithRoutines =
                DowithTask.ofWithRoutine(
                        memberId,
                        taskCategoryId2,
                        title2,
                        date2,
                        startTime2,
                        Set.of(routineDate2_1, routineDate2_2));

        List<DowithTask> dowithTasks = dowithTaskJpaRepository.saveAll(dowithTasksWithRoutines);
        Set<LocalDate> targetDates = Set.of(date2, routineDate2_1, routineDate2_2);
        entityManager.flush();
        dowithTasks.forEach(
                task -> {
                    // when
                    DowithTask target =
                            dowithTaskJpaRepository
                                    .findDowithTaskAggregate(task.getId())
                                    .orElseThrow(() -> new NoResultException());

                    // then
                    assertThat(target.getMemberId()).isEqualTo(memberId);
                    assertThat(target.getTaskCategoryId()).isEqualTo(taskCategoryId2);
                    assertThat(target.getTitle()).isEqualTo(title2);
                    assertThat(target.getStatus()).isEqualTo(DowithTaskStatus.WAIT);
                    assertThat(target.getDate()).isIn(targetDates);
                    assertThat(target.getStartTime()).isEqualTo(startTime2);
                    assertThat(target.getSuccessDateTime()).isNull();
                    assertThat(target.getCompleteDateTime()).isNull();
                    assertThat(target.getConfirms()).isNull();
                    assertThat(target.getRoutine()).isNotNull();
                    assertThat(target.getRoutine().getRoutineDates().getDates()).isEqualTo(targetDates);
                });
    }

    @Test
    @DisplayName("DowithTask Aggregate 조회 (date 단위 조회)")
    void test_findDowithTaskAggregates() {
        // given
        DowithTask dowithTask = DowithTask.of(memberId, taskCategoryId1, title1, date2, startTime2);
        List<DowithTask> dowithTasksWithRoutines =
                DowithTask.ofWithRoutine(
                        memberId,
                        taskCategoryId2,
                        title2,
                        date2,
                        startTime2,
                        Set.of(routineDate2_1, routineDate2_2));

        // when
        dowithTaskJpaRepository.save(dowithTask);
        dowithTaskJpaRepository.saveAll(dowithTasksWithRoutines);
        entityManager.flush();
        List<DowithTask> dowithTaskAggregates =
                dowithTaskJpaRepository.findAllDowithTaskAggregates(memberId, date2);

        // then
        assertThat(dowithTaskAggregates.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("DowithTask Aggregate 조회 (다수 date 단위 조회)")
    void test_findDowithTaskAggregates2() {
        // given
        DowithTask dowithTask = DowithTask.of(memberId, taskCategoryId1, title1, date2, startTime2);
        List<DowithTask> dowithTasksWithRoutines =
                DowithTask.ofWithRoutine(
                        memberId,
                        taskCategoryId2,
                        title2,
                        date2,
                        startTime2,
                        Set.of(routineDate2_1, routineDate2_2));

        // when
        dowithTaskJpaRepository.save(dowithTask);
        dowithTaskJpaRepository.saveAll(dowithTasksWithRoutines);
        entityManager.flush();
        List<DowithTask> dowithTaskAggregates =
                dowithTaskJpaRepository.findAllDowithTaskAggregates(
                        memberId, Set.of(date2, routineDate2_1));

        // then
        assertThat(dowithTaskAggregates.size()).isEqualTo(3);
    }
}
