package com.LetMeDoWith.LetMeDoWith.application.auth.service;

import com.LetMeDoWith.LetMeDoWith.application.member.service.MemberService;
import com.LetMeDoWith.LetMeDoWith.common.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.common.auth.provider.OidcIdTokenProvider;
import com.LetMeDoWith.LetMeDoWith.domain.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class CreateTokenServiceTest {

    @Mock
    AccessTokenProvider accessTokenProvider;

    @Mock
    OidcIdTokenProvider oidcIdTokenProvider;

    @Mock
    MemberService memberService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    CreateTokenService createTokenService;

    @Test
    @DisplayName("[SUCCESS] 토큰 재발급")
    void createTokenRefreshSuccessTest() {
        // TODO
    }
}
