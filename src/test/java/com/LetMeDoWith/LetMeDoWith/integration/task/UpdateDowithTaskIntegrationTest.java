package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.util.EnumUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskRoutineReqDto;
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

public class UpdateDowithTaskIntegrationTest extends AbstractIntegrationTest {

    static final String UPDATE_DOWITH_TASK_URL = "/api/v1/tasks/dowith";
    static final String UPDATE_DOWITH_TASK_ROUTINE_URL = UPDATE_DOWITH_TASK_URL + "/%d/routine";

    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    TaskCategoryJpaRepository taskCategoryJpaRepository;

    private TaskCategory taskCategory;
    private TaskCategory taskCategory2;

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();
        taskCategoryJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        taskCategory = taskCategoryJpaRepository.save(TaskCategory.of(
                "test category 1", TaskCategory.TaskCategoryCreationType.COMMON, "test", this.requestMember.getId()));

        taskCategory2 = taskCategoryJpaRepository.save(TaskCategory.of(
                "test category 2", TaskCategory.TaskCategoryCreationType.COMMON, "test", this.requestMember.getId()));
        this.taskSummary.plusRemainedDowithTaskCount(5);
        this.taskSummaryJpaRepository.saveAndFlush(taskSummary);
    }

    @Test
    @DisplayName("[SUCCESS] 두윗모드 테스크 수정 - 루틴(DAILY) 생성이 포함된 경우")
    void updateDowithTaskWithRoutine1() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "설거지 하기",
                LocalDate.of(2024, 3, 2),
                LocalTime.of(13, 0)));

        // 수정 데이터
        String title = "청소하기";
        Long taskCategoryId = taskCategory2.getId();
        LocalDateTime startDateTime = LocalDateTime.of(2024, 3, 2, 13, 0);
        LocalDate routineStartDate = startDateTime.toLocalDate();
        LocalDate routineEndDate = startDateTime.plusDays(14).toLocalDate(); // 2주
        String cycle = "DAILY";
        boolean isExcludeHolidays = false;

        Set<LocalDate> allDateSet = new HashSet<>();
        for (int i = 0; i <= 14; i++) {
            allDateSet.add(routineStartDate.plusDays(i));
        }

        // when
        UpdateDowithTaskReqDto requestBody = UpdateDowithTaskReqDto.builder()
                .title(title)
                .taskCategoryId(taskCategoryId)
                .date(startDateTime.toLocalDate())
                .startTime(startDateTime.toLocalTime())
                .routineCondition(new UpdateDowithTaskReqDto.UpdateDowithTaskRoutineCondition(
                        routineStartDate, routineEndDate, cycle, null, isExcludeHolidays))
                .build();
        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.put(UPDATE_DOWITH_TASK_URL + "/" + dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));
        dowithTaskJpaRepository.flush();
        Optional<DowithTask> opDowithTask = dowithTaskJpaRepository.findById(dowithTask.getId());

        // then
        resultActions.andExpect(status().isOk());
        assertThat(opDowithTask).isPresent();
        DowithTask savedDowithTask = opDowithTask.get();

        assertThat(savedDowithTask.getRoutine()).isNotNull();
        DowithTaskRoutine savedDowithTaskRoutine = savedDowithTask.getRoutine();

        List<DowithTask> dowithTasks = dowithTaskJpaRepository.findAllDowithTaskAggregates(savedDowithTaskRoutine);
        for (DowithTask task : dowithTasks) {
            assertThat(task.getDate()).isIn(allDateSet);
            assertThat(task.getMemberId()).isEqualTo(this.requestMember.getId());
            assertThat(task.getTaskCategoryId()).isEqualTo(taskCategoryId);
            assertThat(task.getTitle()).isEqualTo(title);
            assertThat(task.getStatus()).isEqualTo(DowithTaskStatus.WAIT);
            assertThat(task.getStartTime()).isEqualTo(startDateTime.toLocalTime());
            assertThat(task.getRoutine()).isNotNull();
            DowithTaskRoutine routine = task.getRoutine();
            assertThat(routine.getId()).isEqualTo(savedDowithTaskRoutine.getId());
            assertThat(routine.getRangeStartDate()).isEqualTo(routineStartDate);
            assertThat(routine.getRangeEndDate()).isEqualTo(routineEndDate);
            assertThat(routine.getCycle()).isEqualTo(EnumUtil.getEnum(TaskRoutineCycle.class, cycle));
            assertThat(routine.getPattern()).isNull();
            assertThat(routine.isExcludeHolidays()).isEqualTo(isExcludeHolidays);

            allDateSet.remove(task.getDate());
        }
        assertThat(allDateSet).isEmpty();
    }

    @Test
    @DisplayName("[SUCCESS] 두윗모드 테스크 수정 - 루틴 생성이 포함되지 않은 경우")
    void updateDowithTaskWithRoutine2() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "설거지 하기",
                LocalDate.of(2024, 3, 3),
                LocalTime.of(14, 0, 0)));

        String title = "청소하기";
        Long taskCategoryId = null;
        LocalDateTime startDateTime = LocalDateTime.of(2024, 3, 2, 13, 0);

        // when
        UpdateDowithTaskReqDto requestBody = UpdateDowithTaskReqDto.builder()
                .title(title)
                .taskCategoryId(taskCategoryId)
                .date(startDateTime.toLocalDate())
                .startTime(startDateTime.toLocalTime())
                .routineCondition(null)
                .build();
        ResultActions resultActions =
                request(MockMvcRequestBuilders.put(UPDATE_DOWITH_TASK_URL + "/" + dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));
        dowithTaskJpaRepository.flush();
        Optional<DowithTask> opDowithTask = dowithTaskJpaRepository.findById(dowithTask.getId());
        // then
        resultActions.andExpect(status().isOk());
        assertThat(opDowithTask).isPresent();
        DowithTask savedDowithTask = opDowithTask.get();
        assertThat(savedDowithTask.getMemberId()).isEqualTo(this.requestMember.getId());
        assertThat(savedDowithTask.getTaskCategoryId()).isEqualTo(taskCategoryId);
        assertThat(savedDowithTask.getTitle()).isEqualTo(title);
        assertThat(savedDowithTask.getStatus()).isEqualTo(DowithTaskStatus.WAIT);
        assertThat(savedDowithTask.getStartTime()).isEqualTo(startDateTime.toLocalTime());
        assertThat(savedDowithTask.getRoutine()).isNull();
    }

    @Test
    @DisplayName("[SUCCESS] 두윗모드 테스크의 DAILY Routine -> 다른 형태의 DAILY Routine으로 수정")
    void updateDowithTaskWithRoutine3() throws Exception {
        // given
        // 기존 Dowith : 3/2일 등록 3/2일부터 3/14일까지 매일
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

        // when
        // 요청 시간 10일 10:00로 10, 11, 12, 13, 14일이 수정 대상으로 분류되어야함
        setFixedClock(LocalDateTime.of(2024, 3, 10, 10, 0));
        LocalDate startDate = LocalDate.of(2024, 3, 10);
        LocalDate endDate = LocalDate.of(2024, 3, 15);
        String cycle = "DAILY";
        boolean isExcludeHolidays = false;

        // 요청 시간 10:00로 13:00에 시작하는 10일, 11일, 12일 13일 14일이 수정 대상이며
        // 최종적으로 10일 11일 12일 13일 14일 15일(new) 이 새로운 routine으로 엮여야함
        UpdateDowithTaskRoutineReqDto requestBody = UpdateDowithTaskRoutineReqDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .cycle(EnumUtil.getEnum(TaskRoutineCycle.class, cycle))
                .pattern(null)
                .isExcludeHolidays(isExcludeHolidays)
                .build();
        Long id = dowithTasks.stream()
                .filter(e -> e.getDate().isEqual(startDate))
                .toList()
                .get(0)
                .getId();
        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.put(String.format(UPDATE_DOWITH_TASK_ROUTINE_URL, id))
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().isOk());
        // 예전 dowith routine에 엮여 있는 dowith 들의 date 검증
        Set<LocalDate> oldDowithTaskDates = new HashSet<>(List.of(
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 2),
                LocalDate.of(2024, 3, 3),
                LocalDate.of(2024, 3, 4),
                LocalDate.of(2024, 3, 5),
                LocalDate.of(2024, 3, 6),
                LocalDate.of(2024, 3, 7),
                LocalDate.of(2024, 3, 8),
                LocalDate.of(2024, 3, 9)));
        Optional<DowithTask> opOldDowithTask = dowithTaskJpaRepository.findByDate(date);
        assertThat(opOldDowithTask).isPresent();
        DowithTaskRoutine oldDowithTaskRoutine = opOldDowithTask.get().getRoutine();
        List<DowithTask> oldDowithTasks = dowithTaskJpaRepository.findAllDowithTaskAggregates(oldDowithTaskRoutine);
        for (DowithTask task : oldDowithTasks) {
            assertThat(task.getDate()).isIn(oldDowithTaskDates);
            oldDowithTaskDates.remove(task.getDate());
        }
        assertThat(oldDowithTaskDates).isEmpty();

        // 새로운 dowith routine에 역여 있는 dowith 들의 date 검증
        Set<LocalDate> newDowithTaskDates = new HashSet<>(List.of(
                LocalDate.of(2024, 3, 10),
                LocalDate.of(2024, 3, 11),
                LocalDate.of(2024, 3, 12),
                LocalDate.of(2024, 3, 13),
                LocalDate.of(2024, 3, 14),
                LocalDate.of(2024, 3, 15)));
        Optional<DowithTask> opNewDowithTask = dowithTaskJpaRepository.findByDate(LocalDate.of(2024, 3, 10));
        assertThat(opNewDowithTask).isPresent();
        DowithTaskRoutine newDowithTaskRoutine = opNewDowithTask.get().getRoutine();
        assertThat(newDowithTaskRoutine.getId()).isNotEqualTo(oldDowithTaskRoutine.getId());

        List<DowithTask> newDowithTasks = dowithTaskJpaRepository.findAllDowithTaskAggregates(newDowithTaskRoutine);
        for (DowithTask task : newDowithTasks) {
            assertThat(task.getDate()).isIn(newDowithTaskDates);
            newDowithTaskDates.remove(task.getDate());
        }
        assertThat(newDowithTaskDates).isEmpty();
        assertThat(newDowithTaskRoutine.getRangeStartDate()).isEqualTo(startDate);
        assertThat(newDowithTaskRoutine.getRangeEndDate()).isEqualTo(endDate);
        assertThat(newDowithTaskRoutine.getCycle().getCode()).isEqualTo(cycle);
        assertThat(newDowithTaskRoutine.getPattern()).isNull();
        assertThat(newDowithTaskRoutine.isExcludeHolidays()).isEqualTo(isExcludeHolidays);
    }
}
