package com.LetMeDoWith.LetMeDoWith.util;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.LetMeDoWith.LetMeDoWith.dto.valueObject.AccessTokenVO;
import com.LetMeDoWith.LetMeDoWith.provider.AuthTokenProvider;

@SpringBootTest
public class AuthTokenProviderTest {

	@Autowired
	private AuthTokenProvider authTokenProvider;

	private static Long MEMBER_ID = 1L;


	@Test
	@DisplayName("Access Token 발급 후 검증 성공")
	void validateAccessToken() {

		// given
		AccessTokenVO accessTokenVO = authTokenProvider.createAccessToken(MEMBER_ID);
		System.out.println(accessTokenVO.token());

		// when
		Long memberId = authTokenProvider.validateToken(accessTokenVO.token(), AuthTokenProvider.TokenType.ATK);

		// then
		Assertions.assertThat(memberId).isEqualTo(MEMBER_ID);

	}
}
