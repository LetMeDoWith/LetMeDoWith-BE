package com.LetMeDoWith.LetMeDoWith.domain.notification.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.notification.enums.NotificationTemplateCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "deep_link", nullable = true, columnDefinition = "TEXT")
    private String deepLink;

    @Column(name = "confirmed_yn", nullable = false)
    private Yn isConfirmed = Yn.FALSE;

    @Column(name = "confirm_date_time", nullable = true)
    private LocalDateTime confirmDateTime;

    @Column(name = "notification_template_code", nullable = true)
    private NotificationTemplateCode notificationTemplateCode;

    public static Notification of(String memberId, String title, String body, String deepLink, NotificationTemplateCode notificationTemplateCode) {
        return Notification.builder()
                .memberId(memberId)
                .title(title)
                .body(body)
                .deepLink(deepLink)
                .notificationTemplateCode(notificationTemplateCode)
                .build();
    }

    public void confirm() {
        this.confirmDateTime = SystemTimeUtil.now();
        this.isConfirmed = Yn.TRUE;
    }
}
