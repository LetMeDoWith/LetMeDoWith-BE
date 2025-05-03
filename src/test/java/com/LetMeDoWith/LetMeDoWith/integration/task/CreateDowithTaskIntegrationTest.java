package com.LetMeDoWith.LetMeDoWith.integration.task;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_NOT_AVAIL_START_TIME;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.TaskCompleteLevel;
import com.LetMeDoWith.LetMeDoWith.common.util.DateTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateDowithTaskReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class CreateDowithTaskIntegrationTest {
    
    static final String BASE_URL = "/api/v1/task/dowith";
    static final String CREATE_DOWITH_TASK = "";
    
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    MemberJpaRepository memberJpaRepository;
    @Autowired
    AccessTokenProvider accessTokenProvider;
    @Autowired
    DowithTaskJpaRepository dowithTaskJpaRepository;
    
    
    private Member member;
    private AccessToken memberAccessToken;
    
    @BeforeEach
    void beforeEach() {
        memberJpaRepository.deleteAll();
        dowithTaskJpaRepository.deleteAll();
        
        member = memberJpaRepository.save(Member.builder()
                                                .status(MemberStatus.NORMAL)
                                                .taskCompleteLevel(TaskCompleteLevel.AVERAGE)
                                                .nickname("test")
                                                .selfDescription("test description")
                                                .gender(Gender.MALE)
                                                .dateOfBirth(LocalDate.of(1995, 11, 4))
                                                .type(MemberType.USER)
                                                .build());
        memberAccessToken = accessTokenProvider.createAccessToken(member.getId());
    }
    
    private ResultActions requestCreateDowithTask(AccessToken accessToken,
                                                  CreateDowithTaskReqDto requestBody)
        throws Exception {
        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("AUTHORIZATION", "Bearer" + accessToken.getToken());
        
        return mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + CREATE_DOWITH_TASK)
                                                     .headers(new HttpHeaders(headerMap))
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .accept(MediaType.APPLICATION_JSON)
                                                     .characterEncoding(StandardCharsets.UTF_8)
                                                     .content(objectMapper.writeValueAsString(
                                                         requestBody)))
                      .andDo(System.out::println);
    }
    
    @Test
    @DisplayName("[SUCCESS] 성공 - 루틴이 없는 경우")
    void createDowithTask() throws Exception {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", null, startDateTime,
                                                                        Boolean.FALSE,
                                                                        null);
        
        // when
        ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
        
        // then
        resultActions.andExpect(status().is2xxSuccessful())
                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].id").exists())
                     .andExpect(
                         jsonPath("$.data.dowithTaskDtos[0].taskCategoryId").value(requestBody.taskCategoryId()))
                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].title").value(requestBody.title()))
                     .andExpect(
                         jsonPath("$.data.dowithTaskDtos[0].status").value(DowithTaskStatus.WAIT.getCode()))
                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].date").value(
                         DateTimeUtil.toFormatString(startDateTime.toLocalDate())))
                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].startTime").value(
                         DateTimeUtil.toFormatString(startDateTime.toLocalTime())))
                     .andExpect(jsonPath("$.data.dowithTaskDtos[0].isRoutine").value(Boolean.FALSE))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[SUCCESS] 성공 - 루틴이 있는 경우")
    void createDowithTaskWithRoutine() throws Exception {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        LocalDate routineDate1 = startDateTime.plusMonths(3).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1,
                                                    routineDate2);
        Collections.sort(targetDates);
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 1L, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        
        // when
        ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
        
        // then
        for (int i = 0; i < targetDates.size(); i++) {
            resultActions.andExpect(status().is2xxSuccessful())
                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].id").exists())
                         .andExpect(jsonPath(
                             "$.data.dowithTaskDtos[" + i + "].taskCategoryId").value(
                             requestBody.taskCategoryId()))
                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].title").value(
                             requestBody.title()))
                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].status").value(
                             DowithTaskStatus.WAIT.getCode()))
                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].date").value(
                             DateTimeUtil.toFormatString(targetDates.get(i))))
                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].startTime").value(
                             DateTimeUtil.toFormatString(startDateTime.toLocalTime())))
                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].isRoutine").value(
                             Boolean.TRUE))
                         .andExpect(jsonPath("$.data.dowithTaskDtos[" + i + "].routineDates").value(
                             new IsEqual<>(
                                 List.of(DateTimeUtil.toFormatString(targetDates.get(0)),
                                         DateTimeUtil.toFormatString(targetDates.get(1)),
                                         DateTimeUtil.toFormatString(targetDates.get(2)))),
                             List.class))
                         .andDo(System.out::println);
        }
        
    }
    
    @Test
    @DisplayName("[FAIL] Task 카테고리가 존재하지 않는 경우")
    void createDowithTask_taskCategoryNotExist() throws Exception {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 100L, startDateTime,
                                                                        Boolean.FALSE,
                                                                        null);
        
        // when
        ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(
                         jsonPath("$.statusCode").value(DOWITH_TASK_TASK_CATEGORY_NOT_EXIST.getStatusCode()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[FAIL] Task일자에 이미 Task 등록된 경우")
    void createDowithTaskWithRoutine_taskCreateCountExceed1() throws Exception {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        LocalDate routineDate1 = startDateTime.plusMonths(3).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1,
                                                    routineDate2);
        Collections.sort(targetDates);
        
        dowithTaskJpaRepository.saveAndFlush(
            DowithTask.of(member.getId(), 1L, "이미 있던 Task", startDateTime.toLocalDate(),
                          startDateTime.toLocalTime()));
        
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 1L, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        
        // when
        ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
        
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
        
        dowithTaskJpaRepository.saveAndFlush(
            DowithTask.of(member.getId(),
                          1L,
                          "이미 있던 Task",
                          routineDate1,
                          startDateTime.toLocalTime()));
        
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 1L, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        
        // when
        ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_CREATE_COUNT_EXCEED.getStatusCode()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[FAIL] 루틴일자에 과거가 포함된 경우")
    void createDowithTaskWithRoutine_taskNotAvailDate() throws Exception {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1);
        LocalDate routineDate1 = startDateTime.minusMonths(1).toLocalDate();
        LocalDate routineDate2 = startDateTime.plusDays(2).toLocalDate();
        List<LocalDate> targetDates = Arrays.asList(startDateTime.toLocalDate(), routineDate1,
                                                    routineDate2);
        Collections.sort(targetDates);
        
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 1L, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        
        // when
        ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
        
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
        
        CreateDowithTaskReqDto requestBody = new CreateDowithTaskReqDto("테스트", 1L, startDateTime,
                                                                        Boolean.TRUE,
                                                                        List.of(startDateTime.toLocalDate(),
                                                                                routineDate1,
                                                                                routineDate2));
        
        // when
        ResultActions resultActions = requestCreateDowithTask(memberAccessToken, requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_NOT_AVAIL_START_TIME.getStatusCode()))
                     .andDo(System.out::println);
        
    }
}
