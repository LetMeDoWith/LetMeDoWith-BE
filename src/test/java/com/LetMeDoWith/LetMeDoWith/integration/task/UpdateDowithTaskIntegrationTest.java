package com.LetMeDoWith.LetMeDoWith.integration.task;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskRoutineReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @DisplayName("[SUCCESS] 두윗모드 테스크 수정 - 루틴 생성이 포함된 경우")
    void updateDowithTaskWithRoutine1() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "설거지 하기",
                LocalDate.of(2024, 3, 2),
                LocalTime.of(13, 0)));

        // when
        List<LocalDate> routineDates =
                List.of(LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 11));
        UpdateDowithTaskReqDto requestBody = UpdateDowithTaskReqDto.builder()
                .title("청소하기")
                .taskCategoryId(taskCategory2.getId())
                .startDateTime(LocalDateTime.of(2024, 3, 3, 14, 0))
                .routineDates(routineDates)
                .build();
        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.put(UPDATE_DOWITH_TASK_URL + "/" + dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().isOk());
        DowithTask savedTask = dowithTaskJpaRepository
                .findById(dowithTask.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 Task가 존재하지 않습니다."));
        List<DowithTask> dowithTasks =
                dowithTaskJpaRepository.findAllDowithTaskAggregates(savedTask.getRoutine()).stream()
                        .sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
                        .toList();
        assertThat(this.taskSummaryJpaRepository
                .findById(this.taskSummary.getId())
                .get()
                .getRemainedDowithTaskCount())
                .isEqualTo(3);
        for (int i = 0; i < dowithTasks.size(); i++) {
            DowithTask task = dowithTasks.get(i);
            assertThat(task.getTitle()).isEqualTo("청소하기");
            assertThat(task.getTaskCategoryId()).isEqualTo(taskCategory2.getId());
            assertThat(task.getDate()).isEqualTo(routineDates.get(i));
            assertThat(task.getStartTime()).isEqualTo(LocalTime.of(14, 0));
            assertThat(task.isRoutine()).isTrue();
            assertThat(task.getRoutineDates()).containsSequence(routineDates);
        }
    }

    @Test
    @DisplayName("[FAIL] 두윗모드 테스크 수정 - Task 등록 가능 개수 초과한 경우")
    void updateDowithTaskWithRoutine2() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                requestMember.getId(), taskCategory.getId(), "설거지 하기", LocalDate.of(2024, 3, 2), LocalTime.of(13, 0)));
        // // 루틴일에 Task 하나 생성
        // dowithTaskJpaRepository.save(
        // DowithTask.of(
        // requestMember.getId(),
        // taskCategory.getId(),
        // "설거지 하기2",
        // LocalDate.of(2024, 3, 10),
        // LocalTime.of(13, 0)));

        // when
        List<LocalDate> routineDates = List.of(
                LocalDate.of(2024, 3, 3),
                LocalDate.of(2024, 3, 10),
                LocalDate.of(2024, 3, 11),
                LocalDate.of(2024, 3, 12),
                LocalDate.of(2024, 3, 13),
                LocalDate.of(2024, 3, 14),
                LocalDate.of(2024, 3, 15));
        UpdateDowithTaskReqDto requestBody = UpdateDowithTaskReqDto.builder()
                .title("청소하기")
                .taskCategoryId(taskCategory2.getId())
                .startDateTime(LocalDateTime.of(2024, 3, 3, 14, 0))
                .routineDates(routineDates)
                .build();
        ResultActions resultActions =
                this.request(MockMvcRequestBuilders.put(UPDATE_DOWITH_TASK_URL + "/" + dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_CREATE_COUNT_EXCEED.getStatusCode()))
                .andDo(System.out::println);
        assertThat(this.taskSummaryJpaRepository
                .findById(this.taskSummary.getId())
                .get()
                .getRemainedDowithTaskCount())
                .isEqualTo(5);
    }

    @Test
    @DisplayName("[SUCCESS] 두윗모드 테스크 수정 - 루틴 생성이 포함되지 않은 경우")
    void updateDowithTaskWithRoutine3() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(
                this.requestMember.getId(),
                taskCategory.getId(),
                "설거지 하기",
                LocalDate.of(2024, 3, 2),
                LocalTime.of(13, 0)));

        // when
        UpdateDowithTaskReqDto requestBody = UpdateDowithTaskReqDto.builder()
                .title("청소하기")
                .taskCategoryId(taskCategory2.getId())
                .startDateTime(LocalDateTime.of(2024, 3, 3, 14, 0))
                .routineDates(null)
                .build();
        ResultActions resultActions =
                request(MockMvcRequestBuilders.put(UPDATE_DOWITH_TASK_URL + "/" + dowithTask.getId())
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().isOk());
        DowithTask savedTask = dowithTaskJpaRepository
                .findById(dowithTask.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 Task가 존재하지 않습니다."));

        assertThat(savedTask.getTitle()).isEqualTo("청소하기");
        assertThat(savedTask.getTaskCategoryId()).isEqualTo(taskCategory2.getId());
        assertThat(savedTask.getDate()).isEqualTo(LocalDate.of(2024, 3, 3));
        assertThat(savedTask.getStartTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(savedTask.isRoutine()).isFalse();
        assertThat(this.taskSummaryJpaRepository
                .findById(this.taskSummary.getId())
                .get()
                .getRemainedDowithTaskCount())
                .isEqualTo(5);
    }

    @Test
    @DisplayName("[SUCCESS] 두윗모드 테스크 루틴 수정")
    void updateDowithTaskWithRoutine4() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository
                .saveAll(DowithTask.of(
                        requestMember.getId(),
                        taskCategory.getId(),
                        "설거지 하기",
                        LocalDate.of(2024, 3, 2),
                        LocalTime.of(13, 0),
                        Set.of(
                                LocalDate.of(2024, 3, 2),
                                LocalDate.of(2024, 3, 3),
                                LocalDate.of(2024, 3, 16),
                                LocalDate.of(2024, 3, 17))))
                .stream()
                .filter(task -> task.getDate().equals(LocalDate.of(2024, 3, 2)))
                .findFirst()
                .get();

        // when
        setFixedClock(LocalDateTime.of(2024, 3, 15, 0, 0));
        List<LocalDate> newRoutineDates = List.of(
                LocalDate.of(2024, 3, 2),
                LocalDate.of(2024, 3, 3),
                LocalDate.of(2024, 3, 16),
                LocalDate.of(2024, 3, 20));
        UpdateDowithTaskRoutineReqDto requestBody = UpdateDowithTaskRoutineReqDto.builder()
                .routineDates(newRoutineDates)
                .build();
        ResultActions resultActions = this.request(
                MockMvcRequestBuilders.put(String.format(UPDATE_DOWITH_TASK_ROUTINE_URL, dowithTask.getId()))
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions.andExpect(status().isOk());
        assertThat(dowithTaskJpaRepository.findByDate(LocalDate.of(2024, 3, 17)))
                .isEmpty();

        List<DowithTask> dowithTasks = dowithTaskJpaRepository
                .findAllDowithTaskAggregates(dowithTaskJpaRepository
                        .findById(dowithTask.getId())
                        .get()
                        .getRoutine())
                .stream()
                .sorted((t1, t2) -> t1.getDate().compareTo(t2.getDate()))
                .toList();
        for (int i = 0; i < dowithTasks.size(); i++) {
            DowithTask task = dowithTasks.get(i);
            assertThat(task.getTitle()).isEqualTo("설거지 하기");
            assertThat(task.getTaskCategoryId()).isEqualTo(taskCategory.getId());
            assertThat(task.getDate()).isEqualTo(newRoutineDates.get(i));
            assertThat(task.getStartTime()).isEqualTo(LocalTime.of(13, 0));
            assertThat(task.isRoutine()).isTrue();
            assertThat(task.getRoutineDates()).isEqualTo(new HashSet<>(newRoutineDates));
        }
        assertThat(this.taskSummaryJpaRepository
                .findById(this.taskSummary.getId())
                .get()
                .getRemainedDowithTaskCount())
                .isEqualTo(5);
    }

    @Test
    @DisplayName(
            "[FAIL] 두윗모드 테스크 루틴 수정 - input routineDates 중에서 업데이트 불가한 routine 일자(과거일자)가 DB에 저장된 routine 중 업데이트 불가한 일자와 일치하지 않는 경우")
    void updateDowithTaskWithRoutine5() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository
                .saveAll(DowithTask.of(
                        this.requestMember.getId(),
                        taskCategory.getId(),
                        "설거지 하기",
                        LocalDate.of(2024, 3, 2),
                        LocalTime.of(13, 0),
                        Set.of(
                                LocalDate.of(2024, 3, 2),
                                LocalDate.of(2024, 3, 3),
                                LocalDate.of(2024, 3, 16),
                                LocalDate.of(2024, 3, 17))))
                .stream()
                .filter(task -> task.getDate().equals(LocalDate.of(2024, 3, 2)))
                .findFirst()
                .get();

        // when
        setFixedClock(LocalDateTime.of(2024, 3, 15, 0, 0));
        List<LocalDate> newRoutineDates = List.of(
                LocalDate.of(2024, 3, 2),
                LocalDate.of(2024, 3, 4), // 일치하지 않는 과거 일자
                LocalDate.of(2024, 3, 16),
                LocalDate.of(2024, 3, 20));
        UpdateDowithTaskRoutineReqDto requestBody = UpdateDowithTaskRoutineReqDto.builder()
                .routineDates(newRoutineDates)
                .build();
        ResultActions resultActions = this.request(
                MockMvcRequestBuilders.put(String.format(UPDATE_DOWITH_TASK_ROUTINE_URL, dowithTask.getId()))
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.statusCode").value(INVALID_REQUEST.getStatusCode()))
                .andDo(System.out::println);
        assertThat(this.taskSummaryJpaRepository
                .findById(this.taskSummary.getId())
                .get()
                .getRemainedDowithTaskCount())
                .isEqualTo(5);
    }

    @Test
    @DisplayName("[FAIL] 두윗모드 테스크 루틴 수정 - 수정하는 routineDate 중에 이미 등록된 Task가 있어 등록 가능 개수 초과한 경우")
    void updateDowithTaskWithRoutine6() throws Exception {
        // given
        setFixedClock(LocalDateTime.of(2024, 3, 1, 0, 0));
        DowithTask dowithTask = dowithTaskJpaRepository
                .saveAll(DowithTask.of(
                        this.requestMember.getId(),
                        taskCategory.getId(),
                        "설거지 하기",
                        LocalDate.of(2024, 3, 2),
                        LocalTime.of(13, 0),
                        Set.of(
                                LocalDate.of(2024, 3, 2),
                                LocalDate.of(2024, 3, 3),
                                LocalDate.of(2024, 3, 16),
                                LocalDate.of(2024, 3, 17))))
                .stream()
                .filter(task -> task.getDate().equals(LocalDate.of(2024, 3, 2)))
                .findFirst()
                .get();

        // when
        setFixedClock(LocalDateTime.of(2024, 3, 15, 0, 0));
        List<LocalDate> newRoutineDates = List.of(
                LocalDate.of(2024, 3, 2),
                LocalDate.of(2024, 3, 3),
                LocalDate.of(2024, 3, 16),
                LocalDate.of(2024, 3, 20), // 17일 대체
                LocalDate.of(2024, 3, 21), // 새로 추가
                LocalDate.of(2024, 3, 22), // 새로 추가
                LocalDate.of(2024, 3, 23), // 새로 추가
                LocalDate.of(2024, 3, 24), // 새로 추가
                LocalDate.of(2024, 3, 25), // 새로 추가
                LocalDate.of(2024, 3, 26)); // 새로 추가
        UpdateDowithTaskRoutineReqDto requestBody = UpdateDowithTaskRoutineReqDto.builder()
                .routineDates(newRoutineDates)
                .build();
        ResultActions resultActions = this.request(
                MockMvcRequestBuilders.put(String.format(UPDATE_DOWITH_TASK_ROUTINE_URL, dowithTask.getId()))
                        .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_CREATE_COUNT_EXCEED.getStatusCode()))
                .andDo(System.out::println);
        assertThat(this.taskSummaryJpaRepository
                .findById(this.taskSummary.getId())
                .get()
                .getRemainedDowithTaskCount())
                .isEqualTo(5);
    }
}
