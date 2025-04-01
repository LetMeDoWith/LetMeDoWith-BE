package com.LetMeDoWith.LetMeDoWith.integration.task;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.TaskCompleteLevel;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.TaskCategoryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskRoutineReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UpdateDowithTaskIntegrationTest {
    
    static final String BASE_URL = "/api/v1/task/dowith";
    
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
    @Autowired
    TaskCategoryJpaRepository taskCategoryJpaRepository;
    
    private Member member;
    private AccessToken memberAccessToken;
    private TaskCategory taskCategory;
    private TaskCategory taskCategory2;
    
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
        
        taskCategory = taskCategoryJpaRepository.save(TaskCategory.of("test category 1",
                                                                      TaskCategory.TaskCategoryCreationType.COMMON,
                                                                      "test",
                                                                      member.getId()));
        
        taskCategory2 = taskCategoryJpaRepository.save(TaskCategory.of("test category 2",
                                                                       TaskCategory.TaskCategoryCreationType.COMMON,
                                                                       "test",
                                                                       member.getId()));
    }
    
    // DowithTask 수정
    private ResultActions requestUpdateDowithTask(UpdateDowithTaskReqDto requestBody)
        throws Exception {
        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("AUTHORIZATION", "Bearer" + memberAccessToken.getToken());
        
        return mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL)
                                                     .headers(new HttpHeaders(headerMap))
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .accept(MediaType.APPLICATION_JSON)
                                                     .characterEncoding(StandardCharsets.UTF_8)
                                                     .content(objectMapper.writeValueAsString(
                                                         requestBody))
                      
                      )
                      .andDo(System.out::println);
    }
    
    // DowithTask 루틴 수정
    private ResultActions requestUpdateDowithTaskRoutine(
        UpdateDowithTaskRoutineReqDto requestBody)
        throws Exception {
        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("AUTHORIZATION", "Bearer" + memberAccessToken.getToken());
        
        return mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/routine")
                                                     .headers(new HttpHeaders(headerMap))
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .accept(MediaType.APPLICATION_JSON)
                                                     .characterEncoding(StandardCharsets.UTF_8)
                                                     .content(objectMapper.writeValueAsString(
                                                         requestBody))
                      
                      )
                      .andDo(System.out::println);
    }
    
    @Test
    @DisplayName("[SUCCESS] 두윗모드 테스크 수정 - 루틴 생성이 포함된 경우")
    void updateDowithTaskWithRoutine1() throws Exception {
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(member.getId(),
                                                                           taskCategory.getId(),
                                                                           "설거지 하기",
                                                                           LocalDate.of(2024, 3, 2),
                                                                           LocalTime.of(13, 0)));
        
        // when
        UpdateDowithTaskReqDto requestBody = UpdateDowithTaskReqDto.builder()
                                                                   .dowithTaskId(dowithTask.getId())
                                                                   .title("청소하기")
                                                                   .taskCategoryId(taskCategory2.getId())
                                                                   .startDateTime(LocalDateTime.of(
                                                                       2024,
                                                                       3,
                                                                       3,
                                                                       14,
                                                                       0))
                                                                   .isRoutineCreate(true)
                                                                   .routineDates(List.of(
                                                                       LocalDate.of(2024, 3, 3),
                                                                       LocalDate.of(2024, 3, 10),
                                                                       LocalDate.of(2024, 3, 11)))
                                                                   .build();
        ResultActions resultActions = requestUpdateDowithTask(requestBody);
        
        // then
        resultActions.andExpect(status().isOk());
        DowithTask savedTask = dowithTaskJpaRepository.findById(dowithTask.getId())
                                                      .orElseThrow(() -> new IllegalArgumentException(
                                                          "해당 Task가 존재하지 않습니다."));
        
        List<LocalDate> routineDates = List.of(LocalDate.of(2024, 3, 3),
                                               LocalDate.of(2024, 3, 10),
                                               LocalDate.of(2024, 3, 11));
        
        List<DowithTask> dowithTasks = dowithTaskJpaRepository.findAllDowithTaskAggregates(
                                                                  savedTask.getRoutine())
                                                              .stream()
                                                              .sorted((t1, t2) -> t1.getDate()
                                                                                    .compareTo(t2.getDate()))
                                                              .toList();
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
    @DisplayName("[FAIL] 두윗모드 테스크 수정 - 루틴일에 Task 등록 가능 개수 초과한 경우")
    void updateDowithTaskWithRoutine2() throws Exception {
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(member.getId(),
                                                                           taskCategory.getId(),
                                                                           "설거지 하기",
                                                                           LocalDate.of(2024, 3, 2),
                                                                           LocalTime.of(13, 0)));
        // 루틴일에 Task 하나 생성
        dowithTaskJpaRepository.save(DowithTask.of(member.getId(),
                                                   taskCategory.getId(),
                                                   "설거지 하기2",
                                                   LocalDate.of(2024, 3, 10),
                                                   LocalTime.of(13, 0)));
        
        // when
        UpdateDowithTaskReqDto requestBody = UpdateDowithTaskReqDto.builder()
                                                                   .dowithTaskId(dowithTask.getId())
                                                                   .title("청소하기")
                                                                   .taskCategoryId(taskCategory2.getId())
                                                                   .startDateTime(LocalDateTime.of(
                                                                       2024,
                                                                       3,
                                                                       3,
                                                                       14,
                                                                       0))
                                                                   .isRoutineCreate(true)
                                                                   .routineDates(List.of(
                                                                       LocalDate.of(2024, 3, 10),
                                                                       LocalDate.of(2024, 3, 11)))
                                                                   .build();
        ResultActions resultActions = requestUpdateDowithTask(requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_CREATE_COUNT_EXCEED.getStatusCode()))
                     .andDo(System.out::println);
        
    }
    
    @Test
    @DisplayName("[SUCCESS] 두윗모드 테스크 수정 - 루틴 생성이 포함되지 않은 경우")
    void updateDowithTaskWithRoutine3() throws Exception {
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(member.getId(),
                                                                           taskCategory.getId(),
                                                                           "설거지 하기",
                                                                           LocalDate.of(2024, 3, 2),
                                                                           LocalTime.of(13, 0)));
        
        // when
        UpdateDowithTaskReqDto requestBody = UpdateDowithTaskReqDto.builder()
                                                                   .dowithTaskId(dowithTask.getId())
                                                                   .title("청소하기")
                                                                   .taskCategoryId(taskCategory2.getId())
                                                                   .startDateTime(LocalDateTime.of(
                                                                       2024,
                                                                       3,
                                                                       3,
                                                                       14,
                                                                       0))
                                                                   .isRoutineCreate(false)
                                                                   .build();
        ResultActions resultActions = requestUpdateDowithTask(requestBody);
        
        // then
        resultActions.andExpect(status().isOk());
        DowithTask savedTask = dowithTaskJpaRepository.findById(dowithTask.getId())
                                                      .orElseThrow(() -> new IllegalArgumentException(
                                                          "해당 Task가 존재하지 않습니다."));
        
        assertThat(savedTask.getTitle()).isEqualTo("청소하기");
        assertThat(savedTask.getTaskCategoryId()).isEqualTo(taskCategory2.getId());
        assertThat(savedTask.getDate()).isEqualTo(LocalDate.of(2024, 3, 3));
        assertThat(savedTask.getStartTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(savedTask.isRoutine()).isFalse();
        
    }
    
    @Test
    @DisplayName("[SUCCESS] 두윗모드 테스크 루틴 수정")
    void updateDowithTaskWithRoutine4() throws Exception {
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        DowithTask dowithTask = dowithTaskJpaRepository.saveAll(DowithTask.ofWithRoutine(
                                                           member.getId(),
                                                           taskCategory.getId(),
                                                           "설거지 하기",
                                                           LocalDate.of(2024, 3, 2),
                                                           LocalTime.of(13, 0),
                                                           Set.of(LocalDate.of(2024, 3, 2),
                                                                  LocalDate.of(2024, 3, 3),
                                                                  LocalDate.of(2024, 3, 16),
                                                                  LocalDate.of(2024, 3, 17)))).stream().filter(task -> task.getDate()
                                                                                                                           .equals(LocalDate.of(
                                                                                                                               2024,
                                                                                                                               3,
                                                                                                                               2)))
                                                       .findFirst().get();
        
        // when
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 15, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        List<LocalDate> newRoutineDates = List.of(
            LocalDate.of(2024,
                         3,
                         2),
            LocalDate.of(2024,
                         3,
                         3),
            LocalDate.of(2024,
                         3,
                         16),
            LocalDate.of(2024,
                         3,
                         20));
        UpdateDowithTaskRoutineReqDto requestBody = UpdateDowithTaskRoutineReqDto.builder()
                                                                                 .dowithTaskId(
                                                                                     dowithTask.getId())
                                                                                 .routineDates(
                                                                                     newRoutineDates)
                                                                                 .build();
        ResultActions resultActions = requestUpdateDowithTaskRoutine(requestBody);
        
        // then
        resultActions.andExpect(status().isOk());
        assertThat(dowithTaskJpaRepository.findByDate(LocalDate.of(2024, 3, 17))).isEmpty();
        
        List<DowithTask> dowithTasks = dowithTaskJpaRepository.findAllDowithTaskAggregates(
                                                                  dowithTaskJpaRepository.findById(dowithTask.getId()).get().getRoutine())
                                                              .stream()
                                                              .sorted((t1, t2) -> t1.getDate()
                                                                                    .compareTo(t2.getDate()))
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
    }
    
    @Test
    @DisplayName("[FAIL] 두윗모드 테스크 루틴 수정 - input routineDates 중에서 업데이트 불가한 routine 일자(과거일자)가 DB에 저장된 routine 중 업데이트 불가한 일자와 일치하지 않는 경우")
    void updateDowithTaskWithRoutine5() throws Exception {
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        DowithTask dowithTask = dowithTaskJpaRepository.saveAll(DowithTask.ofWithRoutine(
                                                           member.getId(),
                                                           taskCategory.getId(),
                                                           "설거지 하기",
                                                           LocalDate.of(2024, 3, 2),
                                                           LocalTime.of(13, 0),
                                                           Set.of(LocalDate.of(2024, 3, 2),
                                                                  LocalDate.of(2024, 3, 3),
                                                                  LocalDate.of(2024, 3, 16),
                                                                  LocalDate.of(2024, 3, 17)))).stream().filter(task -> task.getDate()
                                                                                                                           .equals(LocalDate.of(
                                                                                                                               2024,
                                                                                                                               3,
                                                                                                                               2)))
                                                       .findFirst().get();
        
        // when
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 15, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        List<LocalDate> newRoutineDates = List.of(
            LocalDate.of(2024,
                         3,
                         2),
            LocalDate.of(2024,
                         3,
                         4), // 일치하지 않는 과거 일자
            LocalDate.of(2024,
                         3,
                         16),
            LocalDate.of(2024,
                         3,
                         20));
        UpdateDowithTaskRoutineReqDto requestBody = UpdateDowithTaskRoutineReqDto.builder()
                                                                                 .dowithTaskId(
                                                                                     dowithTask.getId())
                                                                                 .routineDates(
                                                                                     newRoutineDates)
                                                                                 .build();
        ResultActions resultActions = requestUpdateDowithTaskRoutine(requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(INVALID_REQUEST.getStatusCode()))
                     .andDo(System.out::println);
    }
    
    @Test
    @DisplayName("[FAIL] 두윗모드 테스크 루틴 수정 - 수정하는 routineDate 중에 이미 등록된 Task가 있어 등록 가능 개수 초과한 경우")
    void updateDowithTaskWithRoutine6() throws Exception {
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        DowithTask dowithTask = dowithTaskJpaRepository.saveAll(DowithTask.ofWithRoutine(
                                                           member.getId(),
                                                           taskCategory.getId(),
                                                           "설거지 하기",
                                                           LocalDate.of(2024, 3, 2),
                                                           LocalTime.of(13, 0),
                                                           Set.of(LocalDate.of(2024, 3, 2),
                                                                  LocalDate.of(2024, 3, 3),
                                                                  LocalDate.of(2024, 3, 16),
                                                                  LocalDate.of(2024, 3, 17)))).stream().filter(task -> task.getDate()
                                                                                                                           .equals(LocalDate.of(
                                                                                                                               2024,
                                                                                                                               3,
                                                                                                                               2)))
                                                       .findFirst().get();
        
        // 이미 등록된 Task
        dowithTaskJpaRepository.save(DowithTask.of(member.getId(),
                                                   taskCategory.getId(),
                                                   "설거지 하기2",
                                                   LocalDate.of(2024, 3, 20),
                                                   LocalTime.of(13, 0)));
        
        // when
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 15, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        List<LocalDate> newRoutineDates = List.of(
            LocalDate.of(2024,
                         3,
                         2),
            LocalDate.of(2024,
                         3,
                         3),
            LocalDate.of(2024,
                         3,
                         16),
            LocalDate.of(2024,
                         3,
                         20));
        UpdateDowithTaskRoutineReqDto requestBody = UpdateDowithTaskRoutineReqDto.builder()
                                                                                 .dowithTaskId(
                                                                                     dowithTask.getId())
                                                                                 .routineDates(
                                                                                     newRoutineDates)
                                                                                 .build();
        ResultActions resultActions = requestUpdateDowithTaskRoutine(requestBody);
        
        // then
        resultActions.andExpect(status().is4xxClientError())
                     .andExpect(jsonPath("$.statusCode").value(DOWITH_TASK_CREATE_COUNT_EXCEED.getStatusCode()))
                     .andDo(System.out::println);
    }
    
    
}
