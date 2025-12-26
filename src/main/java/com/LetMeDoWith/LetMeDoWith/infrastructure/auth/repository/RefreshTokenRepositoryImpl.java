package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.repository;

import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.repository.RefreshTokenRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.auth.redisRepository.RefreshTokenRedisRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("!dev")
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Override
    public Optional<RefreshToken> getRefreshToken(String refreshToken) {
        return refreshTokenRedisRepository.findById(refreshToken);
    }

    @Override
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRedisRepository.delete(refreshToken);
    }

    @Override
    public void deleteRefreshTokens(String memberId) {
        refreshTokenRedisRepository.deleteByMemberId(memberId);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRedisRepository.save(refreshToken);
    }
}
