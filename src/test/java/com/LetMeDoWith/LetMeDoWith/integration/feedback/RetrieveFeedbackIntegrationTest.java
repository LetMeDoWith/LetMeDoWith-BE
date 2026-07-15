package com.LetMeDoWith.LetMeDoWith.integration.feedback;

import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplateMessage;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.TaskFeedbackTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.TaskFeedbackTemplateMessageJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTokenJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.*;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveDowithTaskFeedbacksResDto.RetrieveTaskFeedbackDto;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveReceivedDowithTaskFeedbacksResDto.ReceivedFeedbackDto;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveSentDowithTaskFeedbacksResDto.SentFeedbackDto;
import com.fasterxml.jackson.databind.JavaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RetrieveFeedbackIntegrationTest extends AbstractIntegrationTest {

    private static final LocalDateTime FIXED_CLOCK_TIME = LocalDateTime.of(2024, 3, 1, 10, 0);
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 3, 1);
    private static final LocalTime TEST_START_TIME = LocalTime.of(10, 0);
    private static final String TEST_EMOJI_URL = "https://example.com/emoji.png";
    private static final CountryCode TEST_LANGUAGE = CountryCode.KR;
    // TODO - 테스트 FCM 토큰 generator에서 발급 받은 토큰 세팅
    private static final String REGISTERED_FCM_TOKEN =
            "fx5STrP_eh7XIRNiVvNBk_:APA91bHpJ_SvZQTs8SK-Hkl5d8vChDEb2_njBRp-uLtzWU-3_s5W9aoL6OprShJG-ZIU4oSSDD4cfvB0jKb8xUcjvLWyVvhDkiM9DhsdrxhKa0wwrDwx-YI";

    @Autowired
    private DowithTaskJpaRepository dowithTaskRepository;

    @Autowired
    private TaskFeedbackTemplateJpaRepository templateRepository;

    @Autowired
    private TaskFeedbackTemplateMessageJpaRepository templateMessageRepository;

    @Autowired
    private DowithTaskFeedbackJpaRepository feedbackRepo;

    @Autowired
    private NotificationTemplateJpaRepository notificationTemplateRepository;

    @Autowired
    private NotificationTokenJpaRepository notificationTokenRepository;

    private DowithTask dowithTask;
    private TaskFeedbackTemplate template1, template2;
    private TaskFeedbackTemplateMessage templateMessage1, templateMessage2;
    private NotificationTemplate notificationTemplate;
    private NotificationToken notificationToken;

    @Override
    protected void deleteTestData() {
        feedbackRepo.deleteAll();
        templateMessageRepository.deleteAll();
        templateRepository.deleteAll();
        dowithTaskRepository.deleteAll();
        notificationTemplateRepository.deleteAll();
        notificationTokenRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        setFixedClock(FIXED_CLOCK_TIME);
        dowithTask = dowithTaskRepository.save(
                DowithTask.of(requestMember.getId(), null, "테스트 태스크", TEST_DATE, TEST_START_TIME));
        notificationToken =
                notificationTokenRepository.save(NotificationToken.of(requestMember.getId(), REGISTERED_FCM_TOKEN));
        notificationTemplate = notificationTemplateRepository.save(NotificationTemplate.of(
                NotificationTemplateCode.FEEDBACK_RECEIVED_1,
                NotificationType.FEEDBACK,
                "{{senderNickname}}의 잡도리를 받았어요",
                "{{receiverNickname}}, 아직도 안했구나?",
                "letmedowith://home"));
        template1 = templateRepository.save(TaskFeedbackTemplate.builder()
                .emojiUrl(TEST_EMOJI_URL)
                .title("잔소리 템플릿1")
                .description("설명")
                .isActive(Yn.TRUE)
                .notificationTemplateCode(NotificationTemplateCode.FEEDBACK_RECEIVED_1)
                .build());
        templateMessage1 = templateMessageRepository.save(TaskFeedbackTemplateMessage.builder()
                .taskFeedbackTemplate(template1)
                .name("발신자에게 보이는 메시지1")
                .message("잔소리 메시지1")
                .language(TEST_LANGUAGE)
                .build());
        template2 = templateRepository.save(TaskFeedbackTemplate.builder()
                .emojiUrl(TEST_EMOJI_URL)
                .title("잔소리 템플릿2")
                .description("설2")
                .isActive(Yn.TRUE)
                .notificationTemplateCode(NotificationTemplateCode.FEEDBACK_RECEIVED_1)
                .build());
        templateMessage2 = templateMessageRepository.save(TaskFeedbackTemplateMessage.builder()
                .taskFeedbackTemplate(template2)
                .name("발신자에게 보이는 메시지2")
                .message("잔소리 메시지2")
                .language(TEST_LANGUAGE)
                .build());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 템플릿 조회")
    void retrieveFeedbackTemplates_success() throws Exception {
        // given

        // when
        MvcResult mvcResult = this.request(MockMvcRequestBuilders.get("/api/v1/feedbacks/templates")
                        .param("language", TEST_LANGUAGE.name()))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        RetrieveTaskFeedbackTemplatesResDto result =
                this.readResponse(content, RetrieveTaskFeedbackTemplatesResDto.class);

        // then
        assertThat(result.templates()).hasSize(2);
        FeedbackTemplateDto dto1 = result.templates().stream()
                .filter(t -> t.id().equals(template1.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("템플릿1 결과 없음"));
        assertThat(dto1.name()).isEqualTo(templateMessage1.getName());
        assertThat(dto1.message()).isEqualTo(templateMessage1.getMessage());
        assertThat(dto1.emojiUrl()).isEqualTo(template1.getEmojiUrl());

        FeedbackTemplateDto dto2 = result.templates().stream()
                .filter(t -> t.id().equals(template2.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("템플릿2 결과 없음"));
        assertThat(dto2.name()).isEqualTo(templateMessage2.getName());
        assertThat(dto2.message()).isEqualTo(templateMessage2.getMessage());
        assertThat(dto2.emojiUrl()).isEqualTo(template2.getEmojiUrl());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 조회 - taskId 기준")
    void retrieveFeedbackByTaskId_success() throws Exception {
        // 사전 생성
        this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(writeRequestBodyAsString(
                                new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId()))))
                .andExpect(status().isOk());
        MvcResult retrieveResult = this.request(
                        MockMvcRequestBuilders.get("/api/v1/feedbacks/dowith-task/" + dowithTask.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = retrieveResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        RetrieveDowithTaskFeedbacksResDto result =
                this.readPagingResponse(content, RetrieveDowithTaskFeedbacksResDto.class);
        assertThat(result.feedbacks()).isNotEmpty();
        RetrieveTaskFeedbackDto feedback = result.feedbacks().get(0);
        assertThat(feedback.dowithTaskId()).isEqualTo(dowithTask.getId());
        assertThat(feedback.taskFeedbackTemplate().id()).isEqualTo(template1.getId());
        assertThat(feedback.taskFeedbackTemplate().name()).isEqualTo(templateMessage1.getName());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 조회 - templateId 필터 적용 시 해당 템플릿만 조회")
    void retrieveFeedbackByTaskId_withTemplateFilter_success() throws Exception {
        // 서비스 제약(sender 쿨다운)을 우회해 template1·template2 잔소리를 각각 직접 저장
        feedbackRepo.save(
                DowithTaskFeedback.of("sender-a", requestMember.getId(), dowithTask.getId(), template1.getId()));
        feedbackRepo.save(
                DowithTaskFeedback.of("sender-b", requestMember.getId(), dowithTask.getId(), template2.getId()));

        MvcResult retrieveResult = this.request(
                        MockMvcRequestBuilders.get("/api/v1/feedbacks/dowith-task/" + dowithTask.getId())
                                .param("feedbackTemplateId", template1.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();
        RetrieveDowithTaskFeedbacksResDto result = this.readPagingResponse(
                retrieveResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                RetrieveDowithTaskFeedbacksResDto.class);

        assertThat(result.feedbacks()).hasSize(1);
        assertThat(result.feedbacks().get(0).taskFeedbackTemplate().id()).isEqualTo(template1.getId());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 조회 - templateId 필터 적용 시 totalCount도 필터 기준으로 반환")
    void retrieveFeedbackByTaskId_withTemplateFilter_totalCountFiltered() throws Exception {
        // 서비스 제약(sender 쿨다운)을 우회해 template1·template2 잔소리를 각각 직접 저장
        feedbackRepo.save(
                DowithTaskFeedback.of("sender-a", requestMember.getId(), dowithTask.getId(), template1.getId()));
        feedbackRepo.save(
                DowithTaskFeedback.of("sender-b", requestMember.getId(), dowithTask.getId(), template2.getId()));

        MvcResult retrieveResult = this.request(
                        MockMvcRequestBuilders.get("/api/v1/feedbacks/dowith-task/" + dowithTask.getId())
                                .param("feedbackTemplateId", template2.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        String content = retrieveResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JavaType type = objectMapper
                .getTypeFactory()
                .constructParametricType(ResponsePageDto.class, RetrieveDowithTaskFeedbacksResDto.class);
        ResponsePageDto<RetrieveDowithTaskFeedbacksResDto> response = objectMapper.readValue(content, type);

        assertThat(response.totalCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("[SUCCESS] 보낸 잔소리 조회 - dowithTaskStatus 포함")
    void retrieveSentFeedbacks_success() throws Exception {
        this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(writeRequestBodyAsString(
                                new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId()))))
                .andExpect(status().isOk());

        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get("/api/v1/feedbacks/send"))
                .andExpect(status().isOk())
                .andReturn();
        RetrieveSentDowithTaskFeedbacksResDto result = this.readPagingResponse(
                retrieveResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                RetrieveSentDowithTaskFeedbacksResDto.class);

        assertThat(result.feedbacks()).isNotEmpty();
        SentFeedbackDto feedback = result.feedbacks().get(0);
        assertThat(feedback.dowithTaskId()).isEqualTo(dowithTask.getId());
        assertThat(feedback.dowithTaskStatus()).isEqualTo(DowithTaskStatus.WAIT);
        assertThat(feedback.taskFeedbackTemplate().id()).isEqualTo(template1.getId());
        assertThat(feedback.taskFeedbackTemplate().name()).isEqualTo(templateMessage1.getName());
        assertThat(feedback.dowithTaskTitle()).isEqualTo("테스트 태스크");
    }

    @Test
    @DisplayName("[SUCCESS] 받은 잔소리 조회 - receivedAt 포함")
    void retrieveReceivedFeedbacks_success() throws Exception {
        this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(writeRequestBodyAsString(
                                new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId()))))
                .andExpect(status().isOk());

        MvcResult retrieveResult = this.request(MockMvcRequestBuilders.get("/api/v1/feedbacks/received"))
                .andExpect(status().isOk())
                .andReturn();
        RetrieveReceivedDowithTaskFeedbacksResDto result = this.readPagingResponse(
                retrieveResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                RetrieveReceivedDowithTaskFeedbacksResDto.class);

        assertThat(result.feedbacks()).isNotEmpty();
        ReceivedFeedbackDto feedback = result.feedbacks().get(0);
        assertThat(feedback.dowithTaskId()).isEqualTo(dowithTask.getId());
        assertThat(feedback.receivedAt()).isNotNull();
        assertThat(feedback.taskFeedbackTemplate().id()).isEqualTo(template1.getId());
        assertThat(feedback.taskFeedbackTemplate().name()).isEqualTo(templateMessage1.getName());
        assertThat(feedback.dowithTaskTitle()).isEqualTo("테스트 태스크");
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 확인")
    void checkFeedback_success() throws Exception {
        // 사전 생성
        this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks")
                        .content(writeRequestBodyAsString(
                                new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId()))))
                .andExpect(status().isOk());

        // CQRS로 id 조회
        MvcResult retrieveResult = this.request(
                        MockMvcRequestBuilders.get("/api/v1/feedbacks/dowith-task/" + dowithTask.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = retrieveResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        RetrieveDowithTaskFeedbacksResDto result =
                this.readPagingResponse(content, RetrieveDowithTaskFeedbacksResDto.class);
        RetrieveTaskFeedbackDto feedback = result.feedbacks().get(0);
        Long feedbackId = feedback.id();

        // 확인 API
        this.request(MockMvcRequestBuilders.patch("/api/v1/feedbacks/" + feedbackId + "/check"))
                .andExpect(status().isOk());

        // CQRS로 isChecked true 확인
        MvcResult afterCheckResult = this.request(
                        MockMvcRequestBuilders.get("/api/v1/feedbacks/dowith-task/" + dowithTask.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String afterCheckContent = afterCheckResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        RetrieveDowithTaskFeedbacksResDto afterResult =
                this.readPagingResponse(afterCheckContent, RetrieveDowithTaskFeedbacksResDto.class);
        RetrieveTaskFeedbackDto afterFeedback = afterResult.feedbacks().get(0);
        assertThat(afterFeedback.isChecked()).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 집계 조회")
    void aggregateTaskFeedbacks_success() throws Exception {
        // given
        // Template1 잔소리 10개
        List<DowithTaskFeedback> feedbacks = new ArrayList<>();
        int senderIdx = 1;
        for (int i = 0; i < 10; i++) {
            feedbacks.add(DowithTaskFeedback.of(
                    "senderId" + senderIdx, requestMember.getId(), dowithTask.getId(), template1.getId()));
            senderIdx++;
        }

        // Template2 잔소리 5개
        for (int i = 0; i < 5; i++) {
            feedbacks.add(DowithTaskFeedback.of(
                    "senderId" + senderIdx, requestMember.getId(), dowithTask.getId(), template2.getId()));
            senderIdx++;
        }

        feedbackRepo.saveAll(feedbacks);
        feedbackRepo.flush();

        // when
        MvcResult mvcResult = this.request(MockMvcRequestBuilders.get(
                        "/api/v1/feedbacks/dowith-task/" + dowithTask.getId() + "/aggregate"))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveTaskFeedbackAggregateCountResDto result = this.readResponse(
                mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                RetrieveTaskFeedbackAggregateCountResDto.class);

        // then
        assertThat(result.aggregates()).hasSize(2);
        RetrieveTaskFeedbackAggregateCountResDto.AggregateDto aggregate1 = result.aggregates().stream()
                .filter(agg -> agg.feedbackTemplateId().equals(template1.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("템플릿1 집계 결과 없음"));
        assertThat(aggregate1.count()).isEqualTo(10);

        RetrieveTaskFeedbackAggregateCountResDto.AggregateDto aggregate2 = result.aggregates().stream()
                .filter(agg -> agg.feedbackTemplateId().equals(template2.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("템플릿2 집계 결과 없음"));
        assertThat(aggregate2.count()).isEqualTo(5);
    }
}
