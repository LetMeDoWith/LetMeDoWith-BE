package com.LetMeDoWith.LetMeDoWith.integration.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.notification.dto.RetrieveNotificationsResDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RetrieveNotificationIntegrationTest extends AbstractIntegrationTest {

    private static final String RETRIEVE_API_URL = "/api/v1/notifications";

    @Autowired
    NotificationJpaRepository notificationJpaRepository;

    @Override
    protected void deleteTestData() {
        notificationJpaRepository.deleteAll();
    }

    @Override
    protected void createTestData() {
        setFixedClock(LocalDateTime.of(2024, 3, 1, 10, 0));
        notificationJpaRepository.save(Notification.of(
                requestMember.getId(),
                NotificationType.NORMAL,
                "normal-1",
                "body-1",
                "app::normal-1",
                "https://example.com/1.png",
                null,
                Yn.FALSE));

        setFixedClock(LocalDateTime.of(2024, 3, 1, 11, 0));
        notificationJpaRepository.save(Notification.of(
                requestMember.getId(),
                NotificationType.NORMAL,
                "normal-2",
                "body-2",
                "app::normal-2",
                null,
                null,
                Yn.TRUE));

        setFixedClock(LocalDateTime.of(2024, 3, 1, 12, 0));
        notificationJpaRepository.save(Notification.of(
                requestMember.getId(),
                NotificationType.EVENT,
                "event-1",
                "event-body",
                "app::event-1",
                "https://example.com/event.png",
                null,
                Yn.FALSE));
    }

    @Test
    @DisplayName("[SUCCESS] 알림 목록 조회 - NORMAL 타입 필터 및 최신순")
    void retrieveNormalNotificationsTest() throws Exception {
        String url = RETRIEVE_API_URL + "?type=" + NotificationType.NORMAL.getCode() + "&page=0&size=10";

        MvcResult result = this.request(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveNotificationsResDto res =
                this.readPagingResponse(result.getResponse().getContentAsString(), RetrieveNotificationsResDto.class);

        assertEquals(2, res.notifications().size());
        assertEquals("normal-2", res.notifications().get(0).title());
        assertEquals("normal-1", res.notifications().get(1).title());
        assertEquals(true, res.notifications().get(0).isConfirmed());
        assertEquals("https://example.com/1.png", res.notifications().get(1).image());
    }

    @Test
    @DisplayName("[SUCCESS] 알림 목록 조회 - EVENT 타입 필터")
    void retrieveEventNotificationsTest() throws Exception {
        String url = RETRIEVE_API_URL + "?type=" + NotificationType.EVENT.getCode() + "&page=0&size=10";

        MvcResult result = this.request(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andReturn();

        RetrieveNotificationsResDto res =
                this.readPagingResponse(result.getResponse().getContentAsString(), RetrieveNotificationsResDto.class);

        assertEquals(1, res.notifications().size());
        assertEquals("event-1", res.notifications().get(0).title());
    }
}
