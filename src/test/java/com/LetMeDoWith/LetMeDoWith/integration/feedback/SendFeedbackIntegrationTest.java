package com.LetMeDoWith.LetMeDoWith.integration.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.dto.FailResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.DowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.TaskFeedbackTemplateMessage;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.DowithTaskFeedbackJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.TaskFeedbackTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.persistence.jpaRepository.TaskFeedbackTemplateMessageJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTokenJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.DowithTaskJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.CreateDowithFeedbackReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveDowithTaskFeedbacksResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveDowithTaskFeedbacksResDto.RetrieveTaskFeedbackDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class SendFeedbackIntegrationTest extends AbstractIntegrationTest {

    private static final LocalDateTime FIXED_CLOCK_TIME = LocalDateTime.of(2024, 3, 1, 10, 0);
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 3, 1);
    private static final LocalTime TEST_START_TIME = LocalTime.of(10, 0);
    private static final String TEST_EMOJI_URL = "https://example.com/emoji.png";
    private static final CountryCode TEST_LANGUAGE = CountryCode.KR;

    private static final String NOTIFICATION_TEMPLATE_CODE_1 = "FEEDBACK_RECEIVED_1";
    private static final String NOTIFICATION_TEMPLATE_CODE_2 = "FEEDBACK_RECEIVED_2";
    private static final String TEST_DEEP_LINK = "letmedowith://home";

    // TODO - 테스트 FCM 토큰 generator에서 발급 받은 토큰 세팅
    private final String REGISTERED_FCM_TOKEN =
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
    private NotificationJpaRepository notificationRepository;

    @Autowired
    private NotificationTokenJpaRepository notificationTokenRepository;

    private Member receiver;
    private DowithTask dowithTask;
    private TaskFeedbackTemplate template1, template2;
    private TaskFeedbackTemplateMessage templateMessage1;

    @Override
    protected void deleteTestData() {
        feedbackRepo.deleteAll();
        notificationRepository.deleteAll();
        notificationTokenRepository.deleteAll();
        templateMessageRepository.deleteAll();
        templateRepository.deleteAll();
        notificationTemplateRepository.deleteAll();
        dowithTaskRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        setFixedClock(FIXED_CLOCK_TIME);

        receiver = memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("receiver")
                .selfDescription("receiver description")
                .gender(Gender.FEMALE)
                .dateOfBirth(LocalDate.of(1996, 1, 1))
                .type(MemberType.USER)
                .build());

        notificationTokenRepository.save(NotificationToken.of(receiver.getId(), REGISTERED_FCM_TOKEN));

        dowithTask =
                dowithTaskRepository.save(DowithTask.of(receiver.getId(), null, "테스트 태스크", TEST_DATE, TEST_START_TIME));

        notificationTemplateRepository.save(NotificationTemplate.of(
                NOTIFICATION_TEMPLATE_CODE_1,
                "{{senderNickname}}의 잡도리를 받았어요",
                "{{receiverNickname}}, 아직도 안했구나?",
                TEST_DEEP_LINK));
        notificationTemplateRepository.save(NotificationTemplate.of(
                NOTIFICATION_TEMPLATE_CODE_2, "{{senderNickname}}의 잡도리를 받았어요", "너 혹시 잡도리 수집중이니?", TEST_DEEP_LINK));

        template1 = templateRepository.save(TaskFeedbackTemplate.builder()
                .emojiUrl(TEST_EMOJI_URL)
                .title("잔소리 템플릿1")
                .description("설명1")
                .isActive(Yn.TRUE)
                .notificationTemplateCode(NOTIFICATION_TEMPLATE_CODE_1)
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
                .description("설명2")
                .isActive(Yn.TRUE)
                .notificationTemplateCode(NOTIFICATION_TEMPLATE_CODE_2)
                .build());
        templateMessageRepository.save(TaskFeedbackTemplateMessage.builder()
                .taskFeedbackTemplate(template2)
                .name("발신자에게 보이는 메시지2")
                .message("잔소리 메시지2")
                .language(TEST_LANGUAGE)
                .build());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 전송 - DowithTaskFeedback & Notification 적재")
    void sendFeedback_success_persistsFeedbackAndNotification() throws Exception {
        // given
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId());

        // when
        this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                .andExpect(status().isOk());

        // then - DowithTaskFeedback 적재 (sender = requestMember, receiver = dowithTask owner)
        List<DowithTaskFeedback> all = feedbackRepo.findAll();
        assertThat(all).hasSize(1);
        DowithTaskFeedback saved = all.get(0);
        assertThat(saved.getDowithTaskId()).isEqualTo(dowithTask.getId());
        assertThat(saved.getTaskFeedbackTemplateId()).isEqualTo(template1.getId());
        assertThat(saved.getSenderMemberId()).isEqualTo(requestMember.getId());
        assertThat(saved.getReceiverMemberId()).isEqualTo(receiver.getId());
        assertThat(saved.getIsChecked()).isEqualTo(Yn.FALSE);

        // then - Notification 적재 (모든 잔소리는 FCM 발송 + DB 저장)
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);
        Notification savedNotification = notifications.get(0);
        assertThat(savedNotification.getMemberId()).isEqualTo(receiver.getId());
        assertThat(savedNotification.getType()).isEqualTo(NotificationType.FEEDBACK);
        assertThat(savedNotification.getNotificationTemplateCode()).isEqualTo(NOTIFICATION_TEMPLATE_CODE_1);
        assertThat(savedNotification.getDeepLink()).isEqualTo(TEST_DEEP_LINK);
        assertThat(savedNotification.getIsConfirmed()).isEqualTo(Yn.FALSE);
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 전송 - 다른 template으로 보내도 정상 적재")
    void sendFeedback_success_withTemplate2() throws Exception {
        // given
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(), template2.getId());

        // when
        this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                .andExpect(status().isOk());

        // then
        assertThat(feedbackRepo.findAll()).hasSize(1);
        assertThat(feedbackRepo.countByDowithTaskIdAndSenderMemberId(dowithTask.getId(), requestMember.getId()))
                .isEqualTo(1);

        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getNotificationTemplateCode()).isEqualTo(NOTIFICATION_TEMPLATE_CODE_2);
        assertThat(notifications.get(0).getType()).isEqualTo(NotificationType.FEEDBACK);
    }

    @Test
    @DisplayName("[FAIL] 잔소리 전송 - 존재하지 않는 dowithTaskId")
    void sendFeedback_fail_dowithTaskNotFound() throws Exception {
        // given
        Long nonExistentTaskId = 999_999L;
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(nonExistentTaskId, template1.getId());

        // when
        ResultActions resultActions = this.request(
                        MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                .andExpect(status().isBadRequest());

        // then
        FailResponseDto failResponse = this.readFailResponse(resultActions);
        assertThat(failResponse.statusCode()).isEqualTo(FailResponseStatus.INVALID_REQUEST.getStatusCode());
        assertThat(feedbackRepo.findAll()).isEmpty();
    }

    @Test
    @DisplayName("[FAIL] 잔소리 전송 - 존재하지 않는 taskFeedbackTemplateId")
    void sendFeedback_fail_templateNotFound() throws Exception {
        // given
        Long nonExistentTemplateId = 999_999L;
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(), nonExistentTemplateId);

        // when
        ResultActions resultActions = this.request(
                        MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                .andExpect(status().isBadRequest());

        // then
        FailResponseDto failResponse = this.readFailResponse(resultActions);
        assertThat(failResponse.statusCode()).isEqualTo(FailResponseStatus.INVALID_REQUEST.getStatusCode());
        assertThat(feedbackRepo.findAll()).isEmpty();
    }

    @Test
    @DisplayName("[FAIL] 잔소리 전송 - 잔소리 가능 시간 외 (시작 시간 +1h2m)")
    void sendFeedback_fail_outOfFeedbackWindow() throws Exception {
        // given - DowithTask 시작 후 1시간 2분 경과 (가능 시간 [start-1m, start+1h1m] 벗어남)
        setFixedClock(FIXED_CLOCK_TIME.plusHours(1).plusMinutes(2));
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId());

        // when
        ResultActions resultActions = this.request(
                        MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                .andExpect(status().isBadRequest());

        // then
        FailResponseDto failResponse = this.readFailResponse(resultActions);
        assertThat(failResponse.statusCode()).isEqualTo(FailResponseStatus.INVALID_REQUEST.getStatusCode());
        assertThat(feedbackRepo.findAll()).isEmpty();
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 전송 - CQRS 조회 API로 적재 검증")
    void sendFeedback_success_verifiedViaCqrs() throws Exception {
        // given
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId());

        // when - POST 생성
        this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                .andExpect(status().isOk());

        // then - CQRS 조회로 생성 검증
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
        assertThat(feedback.isChecked()).isFalse();
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 전송 - 5회 이내 연속 요청 허용")
    void sendFeedback_success_withinFreeThreshold() throws Exception {
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId());
        for (int i = 0; i < 5; i++) {
            this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("[FAIL] 잔소리 전송 - 5회 초과 후 1분 이내 재시도")
    void sendFeedback_fail_withinOneMinuteAfterThreshold() throws Exception {
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId());
        for (int i = 0; i < 5; i++) {
            this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                    .andExpect(status().isOk());
        }
        ResultActions failResult = this.request(
                        MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                .andExpect(status().isBadRequest());
        FailResponseDto failResponse = this.readFailResponse(failResult);
        assertThat(failResponse.statusCode())
                .isEqualTo(FailResponseStatus.FEEDBACK_SENDING_UNAVAILABLE.getStatusCode());
    }

    @Test
    @DisplayName("[SUCCESS] 잔소리 전송 - 5회 초과 후 1분 경과 시 허용")
    void sendFeedback_success_afterOneMinuteFromThreshold() throws Exception {
        CreateDowithFeedbackReqDto req = new CreateDowithFeedbackReqDto(dowithTask.getId(), template1.getId());
        for (int i = 0; i < 5; i++) {
            this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                    .andExpect(status().isOk());
        }
        setFixedClock(FIXED_CLOCK_TIME.plusMinutes(1).plusSeconds(1));
        this.request(MockMvcRequestBuilders.post("/api/v1/feedbacks").content(writeRequestBodyAsString(req)))
                .andExpect(status().isOk());
    }
}
