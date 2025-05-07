package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.assertj.core.api.Assertions.assertThat;
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
@ActiveProfiles("test") // TODO - 추후 AbstractIntegrationTest에서 설정하도록 변경
@AutoConfigureMockMvc
public class DeleteDowithTaskIntegrationTest {
    
    static final String BASE_URL = "/api/v1/task/dowith";
    private final LocalDate nowDate = LocalDate.now();
    private final LocalDate dateBeforeOneDay = nowDate.minusDays(1);
    private final LocalDate dateBeforeTwoDay = nowDate.minusDays(2);
    private final LocalDate dateAfterOneDay = nowDate.plusDays(1);
    private final LocalDate dateAfterTwoDay = nowDate.plusDays(2);
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
        
        taskCategory = taskCategoryJpaRepository.save(TaskCategory.of("test",
                                                                      TaskCategory.TaskCategoryCreationType.COMMON,
                                                                      "test",
                                                                      member.getId()));
        
    }
    
    private ResultActions requestDeleteDowithTask(Long dowithTaskId, boolean isRoutineInclude)
        throws Exception {
        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("AUTHORIZATION", "Bearer" + memberAccessToken.getToken());
        
        return mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{dowithTaskId}",
                                                             dowithTaskId)
                                                     .param("isRoutineInclude",
                                                            String.valueOf(isRoutineInclude))
                                                     .headers(new HttpHeaders(headerMap))
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .accept(MediaType.APPLICATION_JSON)
                                                     .characterEncoding(StandardCharsets.UTF_8))
                      .andDo(System.out::println);
    }
    
    @Test
    @DisplayName("[SUCCESS] Routine이 없는 Task 삭제")
    void deleteDowithTask1() throws Exception {
        
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(member.getId(),
                                                                           taskCategory.getId(),
                                                                           "test",
                                                                           SystemTimeUtil.nowDate()
                                                                                         .plusDays(1),
                                                                           // 시작시간 :  현재 시간 기준 다음날
                                                                           LocalTime.of(1, 0)));
        
        // when
        ResultActions resultActions = requestDeleteDowithTask(dowithTask.getId(), false);
        
        // then
        resultActions.andExpect(status().isOk());
        assertThat(dowithTaskJpaRepository.findById(dowithTask.getId())).isEmpty();
    }
    
    @Test
    @DisplayName("[FAIL] Routine이 없는 Task 삭제 - 시작시간이 과거인 경우")
    void deleteDowithTask2() throws Exception {
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        DowithTask dowithTask = dowithTaskJpaRepository.save(DowithTask.of(member.getId(),
                                                                           taskCategory.getId(),
                                                                           "test",
                                                                           SystemTimeUtil.now()
                                                                                         .plusDays(1)
                                                                                         .toLocalDate(),
                                                                           // 시작시간 :  과거
                                                                           LocalTime.of(1, 0)));
        
        // when
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 15, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        ResultActions resultActions = requestDeleteDowithTask(dowithTask.getId(), false);
        
        // then
        resultActions.andExpect(status().is4xxClientError());
        assertThat(dowithTaskJpaRepository.findById(dowithTask.getId())).isPresent();
    }
    
    @Test
    @DisplayName("[SUCCESS] Routine이 있는 Task 삭제")
    void deleteDowithTaskWithRoutine() throws Exception {
        // given
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 1, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        Set<LocalDate> routineDateSet = new HashSet<>();
        routineDateSet.add(LocalDate.of(2024, 3, 5)); // 삭제되지 않아야 할 Routine
        routineDateSet.add(LocalDate.of(2024, 3, 7)); // 삭제되지 않아야 할 Routine
        routineDateSet.add(LocalDate.of(2024, 3, 16)); // 삭제되어야 할 Routine
        routineDateSet.add(LocalDate.of(2024, 3, 18)); // 삭제되어야 할 Routine
        List<DowithTask> dowithTasks = dowithTaskJpaRepository.saveAll(DowithTask.ofWithRoutine(
            member.getId(),
            taskCategory.getId(),
            "test",
            LocalDate.of(2024, 3, 15),
            LocalTime.of(1, 0),
            routineDateSet));
        
        Long targetDowithTaskID = dowithTasks.stream()
                                             .filter(task -> task.getDate()
                                                                 .equals(LocalDate.of(2024, 3, 15)))
                                             .toList()
                                             .get(0).getId();
        
        List<DowithTask> toSurviveTasks = dowithTasks.stream()
                                                     .filter(task -> task.getDate()
                                                                         .isBefore(LocalDate.of(2024,
                                                                                                3,
                                                                                                15)))
                                                     .toList();
        
        List<DowithTask> toDeleteTasks = dowithTasks.stream()
                                                    .filter(task -> task.getDate()
                                                                        .isAfter(LocalDate.of(2024,
                                                                                              3,
                                                                                              15)))
                                                    .toList();
        
        // when
        SystemTimeUtil.setClock(Clock.fixed(LocalDateTime.of(2024, 3, 15, 0, 0)
                                                         .toInstant(ZoneOffset.UTC),
                                            ZoneId.of("UTC")));
        ResultActions resultActions = requestDeleteDowithTask(targetDowithTaskID, true);
        
        // then
        resultActions.andExpect(status().isOk());
        
        toSurviveTasks.forEach(task -> assertThat(dowithTaskJpaRepository.findById(task.getId())).isPresent());
        toSurviveTasks.forEach(task -> assertThat(dowithTaskJpaRepository.findById(task.getId())
                                                                         .get()
                                                                         .getRoutine()
                                                                         .getRoutineDates()
                                                                         .getDates()).isEqualTo(Set.of(
            LocalDate.of(2024, 3, 5),
            LocalDate.of(2024, 3, 7),
            LocalDate.of(2024, 3, 15))));
        
        toDeleteTasks.forEach(task -> assertThat(dowithTaskJpaRepository.findById(task.getId())).isEmpty());
        
    }
    
    
}
