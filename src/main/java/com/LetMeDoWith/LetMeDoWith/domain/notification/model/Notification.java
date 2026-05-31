package com.LetMeDoWith.LetMeDoWith.domain.notification.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "notification")
public class Notification extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "deep_link", nullable = true, columnDefinition = "TEXT")
    private String deepLink;

    @Column(name = "image_url", nullable = true, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "confirmed_yn", nullable = false)
    private Yn isConfirmed;

    @Column(name = "confirm_date_time", nullable = true)
    private LocalDateTime confirmDateTime;

    @Column(name = "notification_template_code", nullable = true)
    private String notificationTemplateCode;

    public static Notification of(
            String memberId, String title, String body, String deepLink, String notificationTemplateCode) {
        return of(memberId, NotificationType.NORMAL, title, body, deepLink, null, notificationTemplateCode, Yn.FALSE);
    }

    public static Notification of(
            String memberId,
            NotificationType type,
            String title,
            String body,
            String deepLink,
            String imageUrl,
            String notificationTemplateCode,
            Yn isConfirmed) {
        return Notification.builder()
                .memberId(memberId)
                .type(type)
                .title(title)
                .body(body)
                .deepLink(deepLink)
                .imageUrl(imageUrl)
                .notificationTemplateCode(notificationTemplateCode)
                .isConfirmed(isConfirmed)
                .build();
    }

    public void confirm() {
        if (Yn.TRUE.equals(this.isConfirmed)) {
            return;
        }
        this.confirmDateTime = SystemTimeUtil.now();
        this.isConfirmed = Yn.TRUE;
    }
}
