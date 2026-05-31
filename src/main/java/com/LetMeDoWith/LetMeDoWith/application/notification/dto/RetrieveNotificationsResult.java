package com.LetMeDoWith.LetMeDoWith.application.notification.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.dto.NotificationQueryDto;
import java.time.LocalDateTime;
import java.util.List;

public record RetrieveNotificationsResult(Long totalCount, List<Notification> notifications) {

    public static RetrieveNotificationsResult from(Long totalCount, List<NotificationQueryDto> rows) {
        return new RetrieveNotificationsResult(
                totalCount, rows.stream().map(Notification::from).toList());
    }

    public record Notification(
            Long notificationId,
            String title,
            String body,
            String image,
            String deepLink,
            boolean isConfirmed,
            LocalDateTime createdAt) {

        public static Notification from(NotificationQueryDto row) {
            return new Notification(
                    row.id(),
                    row.title(),
                    row.body(),
                    row.imageUrl(),
                    row.deepLink(),
                    Yn.TRUE.equals(row.isConfirmed()),
                    row.createdAt());
        }
    }
}
