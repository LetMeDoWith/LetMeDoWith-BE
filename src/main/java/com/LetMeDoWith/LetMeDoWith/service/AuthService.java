package com.LetMeDoWith.LetMeDoWith.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.LetMeDoWith.LetMeDoWith.dto.responseDto.CreateTokenRefreshResDto;
import com.LetMeDoWith.LetMeDoWith.dto.valueObject.AccessTokenVO;
import com.LetMeDoWith.LetMeDoWith.entity.auth.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.enums.common.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.provider.AuthTokenProvider;
import com.LetMeDoWith.LetMeDoWith.repository.auth.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final AuthTokenProvider authTokenProvider;

	private final RefreshTokenRedisRepository refreshTokenRedisRepository;

	public CreateTokenRefreshResDto createTokenRefresh(String accessToken, String refreshToken, String userAgent) {

		Long memberId = authTokenProvider.validateToken(refreshToken, AuthTokenProvider.TokenType.RTK);

		// Redis에서 RTK info 조회
		RefreshToken savedRefreshToken = refreshTokenRedisRepository.findById(refreshToken)
			.orElseThrow(() -> new RestApiException(FailResponseStatus.TOKEN_EXPIRED_BY_ADMIN));

		// RTK 추가 보안 점검
		if(!savedRefreshToken.getMemberId().equals(memberId)) {
			throw new RestApiException(FailResponseStatus.INVALID_RTK_TOKEN_MEMBER_NOT_MATCHED);
		} else if(!savedRefreshToken.getAccessToken().equals(accessToken)) {
			throw new RestApiException(FailResponseStatus.INVALID_RTK_TOKEN_ATK_NOT_MATCHED);
		}

		// 신규 ATK 발급
		AccessTokenVO accessTokenVO = authTokenProvider.createAccessToken(memberId);

		// 신규 RTK 발급
		RefreshToken newRefreshToken = authTokenProvider.createRefreshToken(memberId, accessTokenVO.token(), userAgent);

		// 기존 RTK info Redis에서 삭제
		refreshTokenRedisRepository.delete(savedRefreshToken);

		return CreateTokenRefreshResDto.builder()
			.accessToken(accessTokenVO.token())
			.accessTokenExpireAt(accessTokenVO.expireAt())
			.refreshToken(newRefreshToken.getToken())
			.build();

	}

}
