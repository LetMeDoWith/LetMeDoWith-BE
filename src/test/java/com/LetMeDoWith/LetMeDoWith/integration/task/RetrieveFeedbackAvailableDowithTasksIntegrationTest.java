package com.LetMeDoWith.LetMeDoWith.integration.task;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.TaskFeedbackTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveFeedbackAvailableDowithTasksResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveFeedbackAvailableDowithTasksResDto.RetrieveFeedbackAvailableDowithTaskResDto;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RetrieveFeedbackAvailableDowithTasksIntegrationTest extends AbstractIntegrationTest {

    private static final String RETRIEVE_FEEDBACK_AVAILABLE_URL = "/api/v1/tasks/dowith/feedback-available";
    private static final LocalDateTime BASE_CREATE_TIME = LocalDateTime.of(2024, 3, 1, 8, 0, 0);

    @Autowired
    private DowithTaskJpaRepository dowithTaskJpaRepository;

    @Autowired
    private DowithTaskFeedbackJpaRepository dowithTaskFeedbackJpaRepository;

    @Autowired
    private TaskFeedbackTemplateJpaRepository taskFeedbackTemplateJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    private Member otherMember;
    private TaskFeedbackTemplate template;

    private DowithTask taskInRange1;
    private DowithTask taskInRange2;
    private DowithTask taskInRange3;
    private DowithTask taskMyOwnInRange;
    private DowithTask taskBoundaryExcluded;
    private DowithTask taskFutureExcluded;
    private DowithTask taskSuccessExcluded;

    private DowithTask taskPrevDayInRange;
    private DowithTask taskPrevDayBoundaryExcluded;
    private DowithTask taskTodayInRange;
    private DowithTask taskTodayFutureExcluded;
    private DowithTask taskMyOwnMidnightInRange;

    @Override
    protected void deleteTestData() {
        dowithTaskFeedbackJpaRepository.deleteAll();
        dowithTaskJpaRepository.deleteAll();
        taskFeedbackTemplateJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        setFixedClock(BASE_CREATE_TIME);

        otherMember = memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("other")
                .selfDescription("other description")
                .gender(Gender.FEMALE)
                .dateOfBirth(LocalDate.of(1995, 11, 4))
                .type(MemberType.USER)
                .build());

        template = taskFeedbackTemplateJpaRepository.save(TaskFeedbackTemplate.builder()
                .emojiUrl("https://example.com/emoji.png")
                .title("잔소리 템플릿")
                .description("설명")
                .isActive(Yn.TRUE)
                .build());

        taskInRange1 =
                DowithTask.of(otherMember.getId(), null, "inRange1", LocalDate.of(2024, 3, 1), LocalTime.of(9, 31));
        taskInRange2 =
                DowithTask.of(otherMember.getId(), null, "inRange2", LocalDate.of(2024, 3, 1), LocalTime.of(9, 45));
        taskInRange3 =
                DowithTask.of(otherMember.getId(), null, "inRange3", LocalDate.of(2024, 3, 1), LocalTime.of(10, 30));
        taskMyOwnInRange = DowithTask.of(
                requestMember.getId(), null, "myOwnInRange", LocalDate.of(2024, 3, 1), LocalTime.of(9, 50));
        taskBoundaryExcluded = DowithTask.of(
                otherMember.getId(), null, "boundaryExcluded", LocalDate.of(2024, 3, 1), LocalTime.of(9, 30));
        taskFutureExcluded = DowithTask.of(
                otherMember.getId(), null, "futureExcluded", LocalDate.of(2024, 3, 1), LocalTime.of(10, 31));

        taskSuccessExcluded = DowithTask.of(
                otherMember.getId(), null, "successExcluded", LocalDate.of(2024, 3, 1), LocalTime.of(10, 0));
        taskSuccessExcluded.success(List.of("https://example.com/success1.jpg"));

        taskPrevDayInRange = DowithTask.of(
                otherMember.getId(), null, "prevDayInRange", LocalDate.of(2024, 3, 1), LocalTime.of(23, 40));
        taskPrevDayBoundaryExcluded = DowithTask.of(
                otherMember.getId(), null, "prevDayBoundaryExcluded", LocalDate.of(2024, 3, 1), LocalTime.of(23, 30));
        taskTodayInRange =
                DowithTask.of(otherMember.getId(), null, "todayInRange", LocalDate.of(2024, 3, 2), LocalTime.of(0, 20));
        taskTodayFutureExcluded = DowithTask.of(
                otherMember.getId(), null, "todayFutureExcluded", LocalDate.of(2024, 3, 2), LocalTime.of(0, 40));
        taskMyOwnMidnightInRange = DowithTask.of(
                requestMember.getId(), null, "myOwnMidnightInRange", LocalDate.of(2024, 3, 1), LocalTime.of(23, 35));

        dowithTaskJpaRepository.saveAll(List.of(
                taskInRange1,
                taskInRange2,
                taskInRange3,
                taskMyOwnInRange,
                taskBoundaryExcluded,
                taskFutureExcluded,
                taskSuccessExcluded,
                taskPrevDayInRange,
                taskPrevDayBoundaryExcluded,
                taskTodayInRange,
                taskTodayFutureExcluded,
                taskMyOwnMidnightInRange));

        DowithTaskFeedback feedback1 = DowithTaskFeedback.of(
                requestMember.getId(), taskInRange1.getMemberId(), taskInRange1.getId(), template.getId());
        DowithTaskFeedback feedback2 = DowithTaskFeedback.of(
                otherMember.getId(), taskInRange1.getMemberId(), taskInRange1.getId(), template.getId());
        DowithTaskFeedback feedback3 = DowithTaskFeedback.of(
                otherMember.getId(), taskInRange2.getMemberId(), taskInRange2.getId(), template.getId());

        dowithTaskFeedbackJpaRepository.saveAll(List.of(feedback1, feedback2, feedback3));
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 가능 두윗 목록 조회 - 시간/상태/피드백 집계")
    void retrieveFeedbackAvailableDowithTasks_basic() throws Exception {
        setFixedClock(LocalDateTime.of(2024, 3, 1, 10, 30, 0));

        ResponsePageDto<RetrieveFeedbackAvailableDowithTasksResDto> pageResponse = requestFeedbackAvailablePage(0, 20);
        RetrieveFeedbackAvailableDowithTasksResDto response = pageResponse.data();

        assertThat(pageResponse.totalCount()).isEqualTo(3L);
        assertThat(pageResponse.totalPage()).isEqualTo(1);

        List<RetrieveFeedbackAvailableDowithTaskResDto> tasks = response.dowithTasks();
        List<Long> ids = tasks.stream()
                .map(RetrieveFeedbackAvailableDowithTaskResDto::id)
                .toList();

        assertThat(ids).contains(taskInRange1.getId(), taskInRange2.getId(), taskInRange3.getId());
        assertThat(ids)
                .doesNotContain(
                        taskMyOwnInRange.getId(),
                        taskBoundaryExcluded.getId(),
                        taskFutureExcluded.getId(),
                        taskSuccessExcluded.getId(),
                        taskPrevDayInRange.getId(),
                        taskTodayInRange.getId());

        RetrieveFeedbackAvailableDowithTaskResDto inRange1 = findById(tasks, taskInRange1.getId());
        assertThat(inRange1.feedbackCount()).isEqualTo(2);
        assertThat(inRange1.myFeedbacks()).hasSize(1);
        assertThat(inRange1.myFeedbacks().get(0).templateId()).isEqualTo(template.getId());

        RetrieveFeedbackAvailableDowithTaskResDto inRange2 = findById(tasks, taskInRange2.getId());
        assertThat(inRange2.feedbackCount()).isEqualTo(1);
        assertThat(inRange2.myFeedbacks()).isEmpty();

        RetrieveFeedbackAvailableDowithTaskResDto inRange3 = findById(tasks, taskInRange3.getId());
        assertThat(inRange3.feedbackCount()).isEqualTo(0);
        assertThat(inRange3.myFeedbacks()).isEmpty();
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 가능 두윗 목록 조회 - 자정 경계")
    void retrieveFeedbackAvailableDowithTasks_midnightRange() throws Exception {
        setFixedClock(LocalDateTime.of(2024, 3, 2, 0, 30, 0));

        ResponsePageDto<RetrieveFeedbackAvailableDowithTasksResDto> pageResponse = requestFeedbackAvailablePage(0, 20);
        RetrieveFeedbackAvailableDowithTasksResDto response = pageResponse.data();

        assertThat(pageResponse.totalCount()).isEqualTo(2L);
        assertThat(pageResponse.totalPage()).isEqualTo(1);

        List<Long> ids = response.dowithTasks().stream()
                .map(RetrieveFeedbackAvailableDowithTaskResDto::id)
                .toList();

        assertThat(ids).contains(taskPrevDayInRange.getId(), taskTodayInRange.getId());
        assertThat(ids)
                .doesNotContain(
                        taskMyOwnMidnightInRange.getId(),
                        taskPrevDayBoundaryExcluded.getId(),
                        taskTodayFutureExcluded.getId(),
                        taskInRange1.getId(),
                        taskInRange2.getId(),
                        taskInRange3.getId());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 가능 두윗 목록 조회 - 내가 만든 두윗 제외")
    void retrieveFeedbackAvailableDowithTasks_excludeMyOwn() throws Exception {
        setFixedClock(LocalDateTime.of(2024, 3, 1, 10, 30, 0));

        ResponsePageDto<RetrieveFeedbackAvailableDowithTasksResDto> pageResponse = requestFeedbackAvailablePage(0, 20);
        List<Long> ids = pageResponse.data().dowithTasks().stream()
                .map(RetrieveFeedbackAvailableDowithTaskResDto::id)
                .toList();

        assertThat(ids).doesNotContain(taskMyOwnInRange.getId());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 가능 두윗 목록 조회 - 결과 없음")
    void retrieveFeedbackAvailableDowithTasks_empty() throws Exception {
        setFixedClock(LocalDateTime.of(2024, 3, 1, 7, 0, 0));

        ResponsePageDto<RetrieveFeedbackAvailableDowithTasksResDto> pageResponse = requestFeedbackAvailablePage(0, 20);
        RetrieveFeedbackAvailableDowithTasksResDto response = pageResponse.data();

        assertThat(pageResponse.totalCount()).isEqualTo(0L);
        assertThat(pageResponse.totalPage()).isEqualTo(0);
        assertThat(response.dowithTasks()).isEmpty();
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 가능 두윗 목록 조회 - 페이지네이션")
    void retrieveFeedbackAvailableDowithTasks_pagination() throws Exception {
        setFixedClock(LocalDateTime.of(2024, 3, 1, 10, 30, 0));

        ResponsePageDto<RetrieveFeedbackAvailableDowithTasksResDto> page0 = requestFeedbackAvailablePage(0, 2);
        ResponsePageDto<RetrieveFeedbackAvailableDowithTasksResDto> page1 = requestFeedbackAvailablePage(1, 2);

        assertThat(page0.totalCount()).isEqualTo(3L);
        assertThat(page1.totalCount()).isEqualTo(3L);
        assertThat(page0.totalPage()).isEqualTo(2);
        assertThat(page1.totalPage()).isEqualTo(2);

        assertThat(page0.data().dowithTasks()).hasSize(2);
        assertThat(page1.data().dowithTasks()).hasSize(1);
    }

    private RetrieveFeedbackAvailableDowithTaskResDto findById(
            List<RetrieveFeedbackAvailableDowithTaskResDto> tasks, Long id) {
        return tasks.stream()
                .filter(task -> task.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    private ResponsePageDto<RetrieveFeedbackAvailableDowithTasksResDto> requestFeedbackAvailablePage(int page, int size)
            throws Exception {
        var result = this.request(MockMvcRequestBuilders.get(RETRIEVE_FEEDBACK_AVAILABLE_URL)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JavaType type = mapper.getTypeFactory()
                .constructParametricType(ResponsePageDto.class, RetrieveFeedbackAvailableDowithTasksResDto.class);
        return mapper.readValue(result.getResponse().getContentAsString(), type);
    }
}
