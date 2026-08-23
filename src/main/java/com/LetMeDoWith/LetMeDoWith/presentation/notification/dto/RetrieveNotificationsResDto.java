package com.LetMeDoWith.LetMeDoWith.presentation.notification.dto;

import com.LetMeDoWith.LetMeDoWith.application.notification.dto.RetrieveNotificationsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "알림 목록 조회 응답")
public record RetrieveNotificationsResDto(@Schema(description = "알림 목록") List<Notification> notifications) {

    public static RetrieveNotificationsResDto from(RetrieveNotificationsResult result) {
        return new RetrieveNotificationsResDto(
                result.notifications().stream().map(Notification::from).toList());
    }

    @Schema(name = "NotificationItem", description = "알림 한 건")
    public record Notification(
            @Schema(description = "알림 ID", example = "1") Long notificationId,
            @Schema(description = "제목", example = "기윤님, 뭐해 안할거야?") String title,
            @Schema(description = "본문", example = "두윗을 빠르게 완료하고 사진을 인증해주세요") String body,
            @Schema(description = "이미지 URL", example = "https://example.com/profile.png") String image,
            @Schema(description = "딥링크", example = "app::deeplink") String deepLink,
            @Schema(description = "확인 여부", example = "true") boolean isConfirmed,
            @Schema(description = "생성일시", example = "2026-01-01T09:00:00") LocalDateTime createdAt) {

        public static Notification from(RetrieveNotificationsResult.Notification result) {
            return new Notification(
                    result.notificationId(),
                    result.title(),
                    result.body(),
                    result.image(),
                    result.deepLink(),
                    result.isConfirmed(),
                    result.createdAt());
        }
    }
}
