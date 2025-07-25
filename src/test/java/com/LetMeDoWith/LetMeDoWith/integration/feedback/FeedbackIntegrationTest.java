package com.LetMeDoWith.LetMeDoWith.integration.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult.TaskFeedbackDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplateMessage;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.TaskFeedbackTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.TaskFeedbackTemplateMessageJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.CreateDowithFeedbackReqDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class FeedbackIntegrationTest extends AbstractIntegrationTest {

    private static final LocalDateTime FIXED_CLOCK_TIME = LocalDateTime.of(2024, 3, 1, 10, 0);
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 3, 1);
    private static final LocalTime TEST_START_TIME = LocalTime.of(10, 0);
    private static final String TEST_MESSAGE = "잔소리 메시지";
    private static final String TEST_EMOJI_URL = "https://example.com/emoji.png";
    private static final CountryCode TEST_LANGUAGE = CountryCode.KR;
    @Autowired
    private DowithTaskJpaRepository dowithTaskRepo;
    @Autowired
    private TaskFeedbackTemplateJpaRepository templateRepo;
    @Autowired
    private TaskFeedbackTemplateMessageJpaRepository templateMsgRepo;
    @Autowired
    private DowithTaskFeedbackJpaRepository feedbackRepo;
    private DowithTask dowithTask;
    private TaskFeedbackTemplate template;
    private TaskFeedbackTemplateMessage templateMsg;

    @Override
    protected void deleteTestData() {
        feedbackRepo.deleteAll();
        templateMsgRepo.deleteAll();
        templateRepo.deleteAll();
        dowithTaskRepo.deleteAll();
    }

    @Override
    protected void createTestData() {
        setFixedClock(FIXED_CLOCK_TIME);
        dowithTask = dowithTaskRepo.save(
                DowithTask.of(requestMember.getId(), null, "테스트 태스크", TEST_DATE, TEST_START_TIME));
        template = templateRepo.save(
                TaskFeedbackTemplate.builder()
                        .emojiUrl(TEST_EMOJI_URL)
                        .title("잔소리 템플릿")
                        .description("설명")
                        .isActive(Yn.TRUE)
                        .build());
        templateMsg = templateMsgRepo.save(
                TaskFeedbackTemplateMessage.builder()
                        .taskFeedbackTemplate(template)
                        .message(TEST_MESSAGE)
                        .language(TEST_LANGUAGE)
                        .build());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 생성")
    void createFeedback_success() throws Exception {
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(),
                template.getId());
        ResultActions resultActions = this.request(
                MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(writeRequestBodyAsString(req)));
        resultActions.andExpect(status().isOk());
        // CQRS 조회로 생성 검증
        MvcResult retrieveResult = this.request(
                MockMvcRequestBuilders.get("/api/v1/feedbacks/")
                        .param("taskId", String.valueOf(dowithTask.getId())))
                .andExpect(status().isOk())
                .andReturn();
        String content = retrieveResult.getResponse().getContentAsString();
        var result = this.readResponse(content, RetrieveTaskFeedbackResult.class);
        assertThat(result.feedbacks()).isNotEmpty();
        var feedback = result.feedbacks().get(0);
        assertThat(feedback.dowithTaskId()).isEqualTo(dowithTask.getId());
        assertThat(feedback.taskFeedbackTemplate().id()).isEqualTo(template.getId());
        assertThat(feedback.isChecked()).isFalse();
    }

    @Test
    @DisplayName("[FAIL] 잔소리 생성 - 1시간 경과 후 시도")
    void createFeedback_fail_timeOver() throws Exception {
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(),
                template.getId());
        ResultActions resultActions = this.request(
                MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(writeRequestBodyAsString(req)));

        setFixedClock(FIXED_CLOCK_TIME.plusMinutes(1));

        ResultActions resultActionsAfter = this.request(
                MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(writeRequestBodyAsString(req)));
        resultActionsAfter.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 조회 - taskId 기준")
    void retrieveFeedbackByTaskId_success() throws Exception {
        // 사전 생성
        this.request(
                MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(
                                writeRequestBodyAsString(
                                        new CreateDowithFeedbackReqDto(dowithTask.getId(), template.getId()))))
                .andExpect(status().isOk());
        MvcResult retrieveResult = this.request(
                MockMvcRequestBuilders.get("/api/v1/feedbacks/")
                        .param("taskId", String.valueOf(dowithTask.getId())))
                .andExpect(status().isOk())
                .andReturn();
        String content = retrieveResult.getResponse().getContentAsString();
        RetrieveTaskFeedbackResult result = this.readResponse(content,
                RetrieveTaskFeedbackResult.class);
        assertThat(result.feedbacks()).isNotEmpty();
        TaskFeedbackDto feedback = result.feedbacks().get(0);
        assertThat(feedback.dowithTaskId()).isEqualTo(dowithTask.getId());
        assertThat(feedback.taskFeedbackTemplate().id()).isEqualTo(template.getId());
    }

    @Test
    @DisplayName("[FAIL] 잔소리 조회 - 파라미터 2개 이상")
    void retrieveFeedback_fail_paramCount() throws Exception {
        this.request(
                MockMvcRequestBuilders.get("/api/v1/feedbacks/")
                        .param("taskId", String.valueOf(dowithTask.getId()))
                        .param("senderId", requestMember.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 확인")
    void checkFeedback_success() throws Exception {
        // 사전 생성
        this.request(
                MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(
                                writeRequestBodyAsString(
                                        new CreateDowithFeedbackReqDto(dowithTask.getId(), template.getId()))))
                .andExpect(status().isOk());
        // CQRS로 id 조회
        MvcResult retrieveResult = this.request(
                MockMvcRequestBuilders.get("/api/v1/feedbacks/")
                        .param("taskId", String.valueOf(dowithTask.getId())))
                .andExpect(status().isOk())
                .andReturn();
        String content = retrieveResult.getResponse().getContentAsString();
        RetrieveTaskFeedbackResult result = this.readResponse(content,
                RetrieveTaskFeedbackResult.class);
        TaskFeedbackDto feedback = result.feedbacks().get(0);
        Long feedbackId = feedback.id();
        // 확인 API
        this.request(MockMvcRequestBuilders.patch("/api/v1/feedbacks/" + feedbackId + "/check"))
                .andExpect(status().isOk());
        // CQRS로 isChecked true 확인
        MvcResult afterCheckResult = this.request(
                MockMvcRequestBuilders.get("/api/v1/feedbacks/")
                        .param("taskId", String.valueOf(dowithTask.getId())))
                .andExpect(status().isOk())
                .andReturn();
        String afterCheckContent = afterCheckResult.getResponse().getContentAsString();
        RetrieveTaskFeedbackResult afterResult = this.readResponse(afterCheckContent,
                RetrieveTaskFeedbackResult.class);
        TaskFeedbackDto afterFeedback = afterResult.feedbacks().get(0);
        assertThat(afterFeedback.isChecked()).isTrue();
    }
}