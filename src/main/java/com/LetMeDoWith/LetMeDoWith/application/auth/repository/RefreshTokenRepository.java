package com.LetMeDoWith.LetMeDoWith.application.auth.repository;

import java.util.Optional;

import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;

public interface RefreshTokenRepository {

	Optional<RefreshToken> getRefreshToken(String refreshToken);

	RefreshToken save(RefreshToken refreshToken);

	void deleteRefreshToken(RefreshToken refreshToken);

	void deleteRefreshTokens(String memberId);

}
