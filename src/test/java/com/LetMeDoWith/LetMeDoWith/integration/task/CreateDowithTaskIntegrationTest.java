package com.LetMeDoWith.LetMeDoWith.integration.task;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateDowithTaskReqDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    }
    
    @Test
    @DisplayName("[SUCCESS] 성공 - 루틴이 없는 경우")
    void createDowithTask() throws Exception {
        // given
        LocalDateTime startDateTime = SystemTimeUtil.now().plusDays(1);
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", null, startDateTime,
                                                                        Boolean.FALSE,
                                                                        null);
        
        // when
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(
                                                                             CREATE_DOWITH_TASK_URL)
                                                                         .content(this.writeRequestBodyAsString(
                                                                             requestBody)));
        
        // then
        resultActions.andExpect(status().is2xxSuccessful())
//                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].id").exists())
//                     .andExpect(
//                         jsonPath("$.data.dowithTaskDtos[0].taskCategoryId").value(requestBody.taskCategoryId()))
//                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].title").value(requestBody.title()))
//                     .andExpect(
//                         jsonPath("$.data.dowithTaskDtos[0].status").value(DowithTaskStatus.WAIT.getCode()))
//                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].date").value(
//                         DateTimeUtil.toFormatString(startDateTime.toLocalDate())))
//                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].startTime").value(
//                         DateTimeUtil.toFormatString(startDateTime.toLocalTime())))
//                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].isRoutine").value(Boolean.FALSE))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[SUCCESS] 성공 - 루틴이 있는 경우")
    void createDowithTaskWithRoutine() throws Exception {
        // given
        LocalDateTime startDateTime = SystemTimeUtil.now().plusDays(1);
        LocalDate routineDate1 = startDateTime.plusMonths(3).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1,
                                                    routineDate2);
        Collections.sort(targetDates);
        
        // when
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", null, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(
                                                                             CREATE_DOWITH_TASK_URL)
                                                                         .content(this.writeRequestBodyAsString(
                                                                             requestBody)));
        
        // then
        for (int i = 0; i < targetDates.size(); i++) {
            resultActions.andExpect(status().is2xxSuccessful())
//                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].id").exists())
//                         .andExpect(jsonPath(
//                             "$.data.dowithTaskDtos[" + i + "].taskCategoryId").value(
//                             requestBody.taskCategoryId()))
//                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].title").value(
//                             requestBody.title()))
//                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].status").value(
//                             DowithTaskStatus.WAIT.getCode()))
//                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].date").value(
//                             DateTimeUtil.toFormatString(targetDates.get(i))))
//                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].startTime").value(
//                             DateTimeUtil.toFormatString(startDateTime.toLocalTime())))
//                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].isRoutine").value(
//                             Boolean.TRUE))
//                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].routineDates").value(
//                             new IsEqual<>(
//                                 List.of(DateTimeUtil.toFormatString(targetDates.get(0)),
//                                         DateTimeUtil.toFormatString(targetDates.get(1)),
//                                         DateTimeUtil.toFormatString(targetDates.get(2)))),
//                             List.class))
                         .andDo(System.out::println);
        }
        
    }
    
    @Test
    @DisplayName("[FAIL] Task 카테고리가 존재하지 않는 경우")
    void createDowithTask_taskCategoryNotExist() throws Exception {
        // given
        LocalDateTime startDateTime = SystemTimeUtil.now().plusDays(1);
        
        // when
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 100L, startDateTime,
                                                                        Boolean.FALSE,
                                                                        null);
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(
                                                                             CREATE_DOWITH_TASK_URL)
                                                                         .content(this.writeRequestBodyAsString(
                                                                             requestBody)));
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(
                         jsonPath("$.statusCode").value(INVALID_REQUEST.getStatusCode()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[FAIL] Task일자에 이미 Task 등록된 경우")
    void createDowithTaskWithRoutine_taskCreateCountExceed1() throws Exception {
        // given
        LocalDateTime startDateTime = SystemTimeUtil.now().plusDays(1);
        LocalDate routineDate1 = startDateTime.plusMonths(3).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1,
                                                    routineDate2);
        Collections.sort(targetDates);
        
        dowithTaskJpaRepository.saveAndFlush(
            DowithTask.of(this.requestMember.getId(), 1L, "이미 있던 Task", startDateTime.toLocalDate(),
                          startDateTime.toLocalTime()));
        
        // when
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", null, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(
                                                                             CREATE_DOWITH_TASK_URL)
                                                                         .content(this.writeRequestBodyAsString(
                                                                             requestBody)));
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_CREATE_COUNT_EXCEED.getStatusCode()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[FAIL] 루틴일자에 이미 Task 등록된 경우")
    void createDowithTaskWithRoutine_taskCreateCountExceed2() throws Exception {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        LocalDate routineDate1 = startDateTime.plusMonths(3).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1,
                                                    routineDate2);
        Collections.sort(targetDates);
        
        // when
        dowithTaskJpaRepository.saveAndFlush(
            DowithTask.of(this.requestMember.getId(),
                          null,
                          "이미 있던 Task",
                          routineDate1,
                          startDateTime.toLocalTime()));
        
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", null, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(
                                                                             CREATE_DOWITH_TASK_URL)
                                                                         .content(this.writeRequestBodyAsString(
                                                                             requestBody)));
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_CREATE_COUNT_EXCEED.getStatusCode()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[FAIL] 루틴일자에 과거가 포함된 경우")
    void createDowithTaskWithRoutine_taskNotAvailDate() throws Exception {
        // given
        LocalDateTime startDateTime = SystemTimeUtil.now().plusDays(1);
        LocalDate routineDate1 = startDateTime.minusMonths(1).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1,
                                                    routineDate2);
        Collections.sort(targetDates);
        
        // when
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", null, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(
                                                                             CREATE_DOWITH_TASK_URL)
                                                                         .content(this.writeRequestBodyAsString(
                                                                             requestBody)));
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_NOT_AVAIL_DATE.getStatusCode()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[FAIL] 일자가 오늘인데, 시작시간이 과거인 경우")
    void createDowithTaskWithRoutine_taskNotAvailStartTime() throws Exception {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().minusMinutes(10);
        LocalDate routineDate1 = startDateTime.plusMonths(1).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1,
                                                    routineDate2);
        Collections.sort(targetDates);
        
        // when
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", null, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(
                                                                             CREATE_DOWITH_TASK_URL)
                                                                         .content(this.writeRequestBodyAsString(
                                                                             requestBody)));
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(INVALID_REQUEST.getStatusCode()))
                     .andDo(System.out::println);
        
    }
}
