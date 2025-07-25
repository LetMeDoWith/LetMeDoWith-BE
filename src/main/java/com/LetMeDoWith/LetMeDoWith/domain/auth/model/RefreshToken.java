package com.LetMeDoWith.LetMeDoWith.domain.auth.model;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder
@Getter
@RedisHash("refreshToken")
@AllArgsConstructor
@RequiredArgsConstructor
public class RefreshToken {

    @Id
    private String token;

    private String accessToken;

    private String memberId;
    private String userAgent;

    @TimeToLive
    private Long expireSec;

    public static RefreshToken of(String memberId, String accessToken, String userAgent, Long expireSec) {
        String refreshToken = UUID.randomUUID().toString();
        return RefreshToken.builder()
                .token(refreshToken)
                .accessToken(accessToken)
                .memberId(memberId)
                .userAgent(userAgent)
                .expireSec(expireSec)
                .build();
    }

    public void checkTokenOwnership(String reqMemberId, String reqAccessToken, String reqUserAgent) {

        if (!reqMemberId.equals(memberId)) {
            throw new RestApiException(FailResponseStatus.INVALID_RTK_TOKEN_MEMBER_NOT_MATCHED);
        }

        if (!reqAccessToken.equals(accessToken)) {
            throw new RestApiException(FailResponseStatus.INVALID_RTK_TOKEN_ATK_NOT_MATCHED);
        }

        if (!reqUserAgent.equals(userAgent)) {
            throw new RestApiException(FailResponseStatus.INVALID_RTK_TOKEN_USER_AGENT_NOT_MATCHED);
        }
    }

    public LocalDateTime calculateExpireAt() {
        return LocalDateTime.now().plusSeconds(this.expireSec);
    }
}
