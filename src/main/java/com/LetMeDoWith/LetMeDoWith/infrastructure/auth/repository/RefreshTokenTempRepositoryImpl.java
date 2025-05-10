package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.repository;

import com.LetMeDoWith.LetMeDoWith.application.auth.repository.RefreshTokenRepository;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.infrastructure.auth.entity.TempRefreshToken;
import com.LetMeDoWith.LetMeDoWith.infrastructure.auth.jpaRepository.TempRefreshTokenJpaRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("dev")
public class RefreshTokenTempRepositoryImpl implements RefreshTokenRepository {

    private final TempRefreshTokenJpaRepository repository;

    @Override
    public Optional<RefreshToken> getRefreshToken(String refreshToken) {
        Optional<TempRefreshToken> opTemp = repository.findById(refreshToken);
        if (opTemp.isEmpty()) return Optional.empty();
        TempRefreshToken tempRefreshToken = opTemp.get();
        LocalDateTime now = LocalDateTime.now();
        if (tempRefreshToken.getExpireAt().isBefore(now)
                || tempRefreshToken.getExpireAt().isEqual(now)) {
            repository.delete(tempRefreshToken);
            return Optional.empty();
        }
        return Optional.of(
                RefreshToken.builder()
                        .token(tempRefreshToken.getToken())
                        .accessToken(tempRefreshToken.getAccessToken())
                        .memberId(tempRefreshToken.getMemberId())
                        .userAgent(tempRefreshToken.getUserAgent())
                        .expireSec(Duration.between(now, tempRefreshToken.getExpireAt()).getSeconds())
                        .build());
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        TempRefreshToken tempRefreshToken = repository.save(TempRefreshToken.from(refreshToken));
        LocalDateTime now = LocalDateTime.now();
        return RefreshToken.builder()
                .token(tempRefreshToken.getToken())
                .accessToken(tempRefreshToken.getAccessToken())
                .memberId(tempRefreshToken.getMemberId())
                .userAgent(tempRefreshToken.getUserAgent())
                .expireSec(Duration.between(now, tempRefreshToken.getExpireAt()).getSeconds())
                .build();
    }

    @Override
    public void deleteRefreshToken(RefreshToken refreshToken) {
        repository.deleteByToken(refreshToken.getToken());
    }

    @Override
    public void deleteRefreshTokens(Long memberId) {
        repository.deleteByMemberId(memberId);
    }
}
