package com.LetMeDoWith.LetMeDoWith.domain.notification.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "notification_token")
public class NotificationToken extends BaseAuditEntity {

    @Column(name = "expired_yn", nullable = false)
    Yn isExpired;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "memberId", nullable = false)
    private String memberId;

    @Column(name = "token", nullable = false)
    private String token;

    public static NotificationToken of(String memberId, String token) {
        return NotificationToken.builder()
                .memberId(memberId)
                .token(token)
                .isExpired(Yn.FALSE)
                .build();
    }

    public void updateToNewToken(String token) {
        this.token = token;
        this.isExpired = Yn.FALSE;
    }

    public boolean isExpired() {
        return this.isExpired == Yn.FALSE;
    }

    public void expireToken() {
        this.isExpired = Yn.TRUE;
    }
}
