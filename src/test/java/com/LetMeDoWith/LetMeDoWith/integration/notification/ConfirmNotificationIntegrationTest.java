package com.LetMeDoWith.LetMeDoWith.integration.notification;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.dto.FailResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ConfirmNotificationIntegrationTest extends AbstractIntegrationTest {

    private static final String CONFIRM_API_URL = "/api/v1/notifications/%d/confirm";

    @Autowired
    NotificationJpaRepository notificationJpaRepository;

    private Notification unconfirmedNotification;
    private Notification confirmedNotification;
    private Notification otherMemberNotification;

    @Override
    protected void deleteTestData() {
        notificationJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        setFixedClock(LocalDateTime.of(2024, 3, 1, 10, 0));
        unconfirmedNotification = notificationJpaRepository.save(Notification.of(
                requestMember.getId(),
                NotificationType.NORMAL,
                "unconfirmed",
                "body",
                "app::unconfirmed",
                null,
                null,
                Yn.FALSE));

        confirmedNotification = notificationJpaRepository.save(Notification.of(
                requestMember.getId(),
                NotificationType.NORMAL,
                "confirmed",
                "body",
                "app::confirmed",
                null,
                null,
                Yn.FALSE));
        confirmedNotification.confirm();
        confirmedNotification = notificationJpaRepository.save(confirmedNotification);

        Member otherMember = memberJpaRepository.save(Member.builder()
                .status(MemberStatus.NORMAL)
                .nickname("other")
                .selfDescription("other")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .type(MemberType.USER)
                .build());

        otherMemberNotification = notificationJpaRepository.save(Notification.of(
                otherMember.getId(),
                NotificationType.NORMAL,
                "other-member",
                "body",
                "app::other",
                null,
                null,
                Yn.FALSE));
    }

    @Test
    @DisplayName("[SUCCESS] 알림 확인 - 미확인 알림")
    void confirmUnconfirmedNotificationTest() throws Exception {
        this.request(MockMvcRequestBuilders.post(CONFIRM_API_URL.formatted(unconfirmedNotification.getId())))
                .andExpect(status().isOk());

        Notification updated = notificationJpaRepository
                .findById(unconfirmedNotification.getId())
                .orElseThrow();
        assertEquals(Yn.TRUE, updated.getIsConfirmed());
        assertNotNull(updated.getConfirmDateTime());
    }

    @Test
    @DisplayName("[SUCCESS] 알림 확인 - 이미 확인한 알림 (멱등)")
    void confirmAlreadyConfirmedNotificationTest() throws Exception {
        LocalDateTime beforeConfirmDateTime = confirmedNotification.getConfirmDateTime();

        this.request(MockMvcRequestBuilders.post(CONFIRM_API_URL.formatted(confirmedNotification.getId())))
                .andExpect(status().isOk());

        Notification updated = notificationJpaRepository
                .findById(confirmedNotification.getId())
                .orElseThrow();
        assertEquals(Yn.TRUE, updated.getIsConfirmed());
        assertEquals(beforeConfirmDateTime, updated.getConfirmDateTime());
    }

    @Test
    @DisplayName("[FAIL] 알림 확인 - 소유 회원이 아닌 경우")
    void confirmOtherMemberNotificationTest() throws Exception {
        var result = this.request(
                        MockMvcRequestBuilders.post(CONFIRM_API_URL.formatted(otherMemberNotification.getId())))
                .andExpect(status().isBadRequest());

        FailResponseDto failResponse = this.readFailResponse(result);
        assertEquals(INVALID_REQUEST.getStatusCode(), failResponse.statusCode());
    }
}
