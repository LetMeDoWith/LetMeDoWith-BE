package com.LetMeDoWith.LetMeDoWith.integration.task;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateDowithTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateDowithTaskWithRoutineReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreateDowithTaskIntegrationTest extends AbstractIntegrationTest {

    static final String CREATE_DOWITH_TASK_URL = "/api/v1/tasks/dowith";

    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;

    @Override
    protected void deleteTestData() {
        dowithTaskJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        this.taskSummary.plusRemainedDowithTaskCount(5);
        this.taskSummaryJpaRepository.saveAndFlush(this.taskSummary);
    }

    @Test
    @DisplayName("[SUCCESS] 성공 - 루틴이 없는 경우")
    void createDowithTask() throws Exception {
        // given
        String title = "테스트";
        LocalDateTime startDateTime = SystemTimeUtil.now().plusDays(1);
        CreateDowithTaskReqDto requestBody =
                new CreateDowithTaskReqDto(title, null, startDateTime.toLocalDate(), startDateTime.toLocalTime());

        // when
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(CREATE_DOWITH_TASK_URL)
                .content(this.writeRequestBodyAsString(requestBody)));

        DowithTask dowithTask = dowithTaskJpaRepository.findAllDowithTaskAggregates(this.requestMember.getId(), startDateTime.toLocalDate()).get(0);

        // then
        assertThat(dowithTask.getTitle()).isEqualTo(title);
        assertThat(dowithTask.getDate()).isEqualTo(startDateTime.toLocalDate());
        assertThat(dowithTask.getStartTime()).isEqualTo(startDateTime.toLocalTime());
        assertThat(dowithTask.getRoutine()).isNull();
        assertThat(dowithTask.getStatus()).isEqualTo(DowithTaskStatus.WAIT);
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andDo(System.out::println);
    }

    @Test
    @DisplayName("[SUCCESS] 성공 - 루틴(DAILY)이 있는 경우")
    void createDowithTaskWithRoutine() throws Exception {
        // given
        String title = "test";
        LocalDateTime startDateTime = SystemTimeUtil.now().plusDays(1);
        LocalDate routineStartDate = startDateTime.toLocalDate();
        LocalDate routineEndDate = startDateTime.plusDays(14).toLocalDate(); // 2주
        String cycle = "DAILY";
        boolean isExcludeHolidays = false;

        Set<LocalDate> allDateSet = new HashSet<>();
        for (int i = 0; i <= 14; i++) {
            allDateSet.add(routineStartDate.plusDays(i));
        }

        // when
        CreateDowithTaskWithRoutineReqDto requestBody = new CreateDowithTaskWithRoutineReqDto(
                title,
                null,
                startDateTime.toLocalDate(),
                startDateTime.toLocalTime(),
                new CreateDowithTaskWithRoutineReqDto.CreateDowithTaskRoutineCondition(
                        routineStartDate,
                        routineEndDate,
                        cycle,
                        null,
                        isExcludeHolidays
                )
        );
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(CREATE_DOWITH_TASK_URL + "/with-routine")
                .content(this.writeRequestBodyAsString(requestBody)));
        DowithTask dowithTask = dowithTaskJpaRepository.findAllDowithTaskAggregates(requestMember.getId(), startDateTime.toLocalDate()).get(0);
        List<DowithTask> dowithTasks = dowithTaskJpaRepository.findAllDowithTaskAggregates(dowithTask.getRoutine());

        // then
        assertThat(dowithTasks.size()).isEqualTo(14);
        for (DowithTask task : dowithTasks) {
            assertThat(task.getDate()).isIn(allDateSet);
            assertThat(task.getTitle()).isEqualTo(title);
            assertThat(task.getDate()).isEqualTo(startDateTime.toLocalDate());
            assertThat(task.getStartTime()).isEqualTo(startDateTime.toLocalTime());
            assertThat(task.getRoutine()).isNull();
            assertThat(task.getStatus()).isEqualTo(DowithTaskStatus.WAIT);
            allDateSet.remove(task.getDate());
        }
        resultActions.andExpect(status().is2xxSuccessful());
//        assertThat(allDateSet).isEmpty();
//        for (int i = 0; i < targetDates.size(); i++) {
//
//                    // .andExpect(jsonPath("$.data.dowithTaskDtos[" + i +
//                    // "].id").exists())
//                    // .andExpect(jsonPath(
//                    // "$.data.dowithTaskDtos[" + i +
//                    // "].taskCategoryId").value(
//                    // requestBody.taskCategoryId()))
//                    // .andExpect(jsonPath("$.data.dowithTaskDtos[" + i +
//                    // "].title").value(
//                    // requestBody.title()))
//                    // .andExpect(jsonPath("$.data.dowithTaskDtos[" + i +
//                    // "].status").value(
//                    // DowithTaskStatus.WAIT.getCode()))
//                    // .andExpect(jsonPath("$.data.dowithTaskDtos[" + i +
//                    // "].date").value(
//                    // DateTimeUtil.toFormatString(targetDates.get(i))))
//                    // .andExpect(jsonPath("$.data.dowithTaskDtos[" + i +
//                    // "].startTime").value(
//                    //
//                    // DateTimeUtil.toFormatString(startDateTime.toLocalTime())))
//                    // .andExpect(jsonPath("$.data.dowithTaskDtos[" + i +
//                    // "].isRoutine").value(
//                    // Boolean.TRUE))
//                    // .andExpect(jsonPath("$.data.dowithTaskDtos[" + i +
//                    // "].routineDates").value(
//                    // new IsEqual<>(
//                    //
//                    // List.of(DateTimeUtil.toFormatString(targetDates.get(0)),
//                    //
//                    // DateTimeUtil.toFormatString(targetDates.get(1)),
//                    //
//                    // DateTimeUtil.toFormatString(targetDates.get(2)))),
//                    // List.class))
//                    .andDo(System.out::println);
//        }
    }

    @Test
    @DisplayName("[FAIL] Task 카테고리가 존재하지 않는 경우")
    void createDowithTask_taskCategoryNotExist() throws Exception {
        // given
        LocalDateTime startDateTime = SystemTimeUtil.now().plusDays(1);

        // when
        CreateDowithTaskReqDto requestBody =
                new CreateDowithTaskReqDto("테스트", 100L, startDateTime.toLocalDate(), startDateTime.toLocalTime());
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(CREATE_DOWITH_TASK_URL)
                .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.statusCode").value(INVALID_REQUEST.getStatusCode()))
                .andDo(System.out::println);
    }

    @Test
    @DisplayName("[FAIL] 일자가 오늘인데, 시작시간이 과거인 경우")
    void createDowithTaskWithRoutine_taskNotAvailStartTime() throws Exception {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().minusMinutes(10);
        LocalDate routineDate1 = startDateTime.plusMonths(1).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1, routineDate2);
        Collections.sort(targetDates);

        // when
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto(
                "테스트",
                null,
                startDateTime.toLocalDate(),
                startDateTime.toLocalTime());
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(CREATE_DOWITH_TASK_URL)
                .content(this.writeRequestBodyAsString(requestBody)));

        // then
        assertThat(this.taskSummaryJpaRepository
                .findById(this.taskSummary.getId())
                .get()
                .getRemainedDowithTaskCount())
                .isEqualTo(5);
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.statusCode").value(INVALID_REQUEST.getStatusCode()))
                .andDo(System.out::println);
    }
}
