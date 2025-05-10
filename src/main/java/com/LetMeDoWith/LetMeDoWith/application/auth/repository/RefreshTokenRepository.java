package com.LetMeDoWith.LetMeDoWith.application.auth.repository;

import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> getRefreshToken(String refreshToken);

    RefreshToken save(RefreshToken refreshToken);

    void deleteRefreshToken(RefreshToken refreshToken);

    void deleteRefreshTokens(Long memberId);
}
