package com.LetMeDoWith.LetMeDoWith.infrastructure.auth;

import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.infrastructure.auth.redisRepository.RefreshTokenRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataRedisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RefreshTokenRefreshTokenRepositoryTest {

    private final String refreshToken = "refreshTokenTestTest";
    private final String accessToken = "accessTokenTestTest";
    private final String memberId = "01HXQ2X7Z7Q6XJX4X2X7Z7Q6XA";
    private final String userAgent = "I-PHONE";

    @Autowired
    private RefreshTokenRedisRepository repository;

    @DisplayName("[SUCCESS] refreshToken Redis 저장 성공")
    @Test
    void refreshTokenCreateSuccessTest() {
        // given
        RefreshToken refreshToken = RefreshToken.builder()
                .token(this.refreshToken)
                .accessToken(this.accessToken)
                .memberId(memberId)
                .userAgent(this.userAgent)
                .expireSec(30L)
                .build();

        // when
        RefreshToken save = repository.save(refreshToken);
        RefreshToken savedRefreshToken = repository.findById(this.refreshToken).get();

        // then
        Assertions.assertThat(savedRefreshToken.getToken()).isEqualTo(this.refreshToken);
        Assertions.assertThat(savedRefreshToken.getAccessToken()).isEqualTo(this.accessToken);
        Assertions.assertThat(savedRefreshToken.getMemberId()).isEqualTo(this.memberId);
        Assertions.assertThat(savedRefreshToken.getUserAgent()).isEqualTo(this.userAgent);
        Assertions.assertThat(savedRefreshToken.getExpireSec()).isEqualTo(30L);
    }
}
