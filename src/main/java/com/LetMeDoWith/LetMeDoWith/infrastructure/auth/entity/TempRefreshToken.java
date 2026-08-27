package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.entity;

import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity(name = "dev_refresh_token")
@NoArgsConstructor
@AllArgsConstructor
public class TempRefreshToken {

    @Id
    @Column(name = "token")
    private String token;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    public static TempRefreshToken from(RefreshToken refreshToken) {
        return TempRefreshToken.builder()
                .token(refreshToken.getToken())
                .accessToken(refreshToken.getAccessToken())
                .memberId(refreshToken.getMemberId())
                .userAgent(refreshToken.getUserAgent())
                .expireAt(LocalDateTime.now().plusSeconds(refreshToken.getExpireSec()))
                .build();
    }
}
