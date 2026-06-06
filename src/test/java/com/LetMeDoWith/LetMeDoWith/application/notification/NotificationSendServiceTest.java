package com.LetMeDoWith.LetMeDoWith.application.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.LetMeDoWith.LetMeDoWith.application.notification.service.NotificationSendService;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTemplateJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTokenJpaRepository;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotificationSendServiceTest {

    // TODO - 테스트 FCM 토큰 generator에서 발급 받은 토큰 세팅
    private final String REGISTERED_FCM_TOKEN =
            "fx5STrP_eh7XIRNiVvNBk_:APA91bHpJ_SvZQTs8SK-Hkl5d8vChDEb2_njBRp-uLtzWU-3_s5W9aoL6OprShJG-ZIU4oSSDD4cfvB0jKb8xUcjvLWyVvhDkiM9DhsdrxhKa0wwrDwx-YI";
    private final String UNREGISTERED_FCM_TOKEN =
            "fx5STrP_eh7XIRNiVvNdk_:APA91bHpJ_SvZQTs8SK-Hkl5d8vChDEb2_njBRp-uLtzWU-3_s5W9aoL6OprShJG-ZIU4oSSDD4cfvB0jKb8xUcjvLWyVvhDkiM9DhsdrxhKa0wwrDwx-YI";

    @Autowired
    NotificationSendService notificationSendService;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    NotificationJpaRepository notificationJpaRepository;

    @Autowired
    NotificationTemplateJpaRepository notificationTemplateJpaRepository;

    @Autowired
    NotificationTokenJpaRepository notificationTokenJpaRepository;

    private Member member;

    private NotificationToken notificationToken;
    private NotificationTemplate notificationTemplate;

    @BeforeEach
    void beforeEach() {
        this.member = memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("test")
                .selfDescription("test description")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1995, 11, 4))
                .type(MemberType.USER)
                .build());
        this.notificationTemplate = notificationTemplateJpaRepository.save(NotificationTemplate.of(
                "TEST_TEMPLATE",
                "테스트 {{testName}}",
                "안녕하세요 {{nickName}}님! 오늘 날씨는 {{weather}}입니다. 테스트 입니다",
                "letmedowith://test"));
    }

    @AfterEach
    void afterEach() {
        notificationTokenJpaRepository.delete(notificationToken);
        notificationTemplateJpaRepository.delete(notificationTemplate);
        notificationJpaRepository.deleteAll();
        memberJpaRepository.delete(member);
    }

    @Test
    @DisplayName("[SUCCESS] 정상 메시지 전송")
    void sendMessage() throws InterruptedException {
        // given
        this.notificationToken =
                notificationTokenJpaRepository.save(NotificationToken.of(member.getId(), REGISTERED_FCM_TOKEN));
        String weather = "맑음";

        // when
        notificationSendService.sendNotification(
                member.getId(),
                notificationTemplate.getCode(),
                Map.of("testName", "테스트 이름"),
                Map.of("nickName", member.getNickname(), "weather", "맑음"),
                true);
        Thread.sleep(1000); // 비동기 처리로 인해 DB에 저장되는 시간이 필요할 수 있음
        Optional<Notification> opNotification = notificationJpaRepository.findByMemberId(member.getId());

        // then
        assertThat(opNotification.isPresent()).isTrue();
        Notification notification = opNotification.get();
        assertThat(notification.getMemberId()).isEqualTo(member.getId());
        assertThat(notification.getTitle()).isEqualTo("테스트 테스트 이름");
        assertThat(notification.getBody())
                .isEqualTo("안녕하세요 " + member.getNickname() + "님! 오늘 날씨는 " + weather + "입니다. 테스트 입니다");
        assertThat(notification.getDeepLink()).isEqualTo("letmedowith://test");
    }

    @Test
    @DisplayName("[FAIL] FCM 토큰이 등록되지 않은 토큰인 경우")
    void sendMessage_fail1() throws InterruptedException {
        // given
        this.notificationToken =
                notificationTokenJpaRepository.save(NotificationToken.of(member.getId(), UNREGISTERED_FCM_TOKEN));
        // when
        try {
            notificationSendService.sendNotification(
                    member.getId(),
                    notificationTemplate.getCode(),
                    Map.of("testName", "테스트 이름"),
                    Map.of("nickName", member.getNickname(), "weather", "맑음"),
                    true);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
        }

        Thread.sleep(1000); // 비동기 처리로 인해 DB에 저장되는 시간이 필요할 수 있음
        Optional<Notification> opNotification = notificationJpaRepository.findByMemberId(member.getId());
        Optional<NotificationToken> opNotificationToken = notificationTokenJpaRepository.findByMemberId(member.getId());
        // then
        assertThat(opNotification.isEmpty()).isTrue();
        assertThat(opNotificationToken.isPresent()).isTrue();
        NotificationToken notificationToken = opNotificationToken.get();
        assertThat(notificationToken.getToken()).isEqualTo(UNREGISTERED_FCM_TOKEN);
        assertThat(notificationToken.isExpired()).isTrue();
    }

    @Test
    @DisplayName("[FAIL] title에 필요한 parameter가 한개라도 없는 경우")
    void sendMessage_fail2() throws InterruptedException {
        // given
        this.notificationToken =
                notificationTokenJpaRepository.save(NotificationToken.of(member.getId(), REGISTERED_FCM_TOKEN));
        // when
        try {
            notificationSendService.sendNotification(
                    member.getId(),
                    notificationTemplate.getCode(),
                    Map.of("wrongKey", "테스트 이름"),
                    Map.of("nickName", member.getNickname()),
                    true);
            Thread.sleep(1000);
        } catch (RestApiException e) {
            assertThat(e.getStatus()).isEqualTo(FailResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Test
    @DisplayName("[FAIL] body에 필요한 parameter가 한개라도 없는 경우")
    void sendMessage_fail3() throws InterruptedException {
        // given
        this.notificationToken =
                notificationTokenJpaRepository.save(NotificationToken.of(member.getId(), REGISTERED_FCM_TOKEN));
        // when
        try {
            notificationSendService.sendNotification(
                    member.getId(),
                    notificationTemplate.getCode(),
                    Map.of("testName", "테스트 이름"),
                    Map.of("nickName", member.getNickname()),
                    true);
            Thread.sleep(1000);
        } catch (RestApiException e) {
            assertThat(e.getStatus()).isEqualTo(FailResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
