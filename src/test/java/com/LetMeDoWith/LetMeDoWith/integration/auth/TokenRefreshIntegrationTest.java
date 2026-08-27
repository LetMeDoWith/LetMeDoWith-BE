package com.LetMeDoWith.LetMeDoWith.integration.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.RefreshTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.service.CreateTokenService;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenRefreshReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class TokenRefreshIntegrationTest extends AbstractIntegrationTest {

    static final String BASE_URL = "/api/v1/auth";
    static final String TOKEN_REFRESH_URL = "/token/refresh";
    static String userAgent = "IPHONE";

    @Autowired
    AccessTokenProvider accessTokenProvider;

    @Autowired
    RefreshTokenProvider refreshTokenProvider;

    @Autowired
    CreateTokenService createTokenService;

    @Override
    protected void deleteTestData() {}

    @Override
    protected void createTestData() {}

    @Test
    @DisplayName("[SUCCESS] 토큰 재발급")
    void refreshTokenSuccessTest() throws Exception {

        // given
        CreateTokenRefreshReqDto requestBody = CreateTokenRefreshReqDto.builder()
                .refreshToken(this.requestMemberRefreshToken.getToken())
                .build();

        // when
        this.setFixedClock(SystemTimeUtil.now().plusDays(30));
        System.out.println(SystemTimeUtil.now());
        ResultActions resultActions = this.request(MockMvcRequestBuilders.post(BASE_URL + TOKEN_REFRESH_URL)
                .content(this.writeRequestBodyAsString(requestBody)));

        // then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberId").value(requestMember.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken.token")
                        .exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken.token")
                        .exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken.expireAt")
                        .exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken.expireAt")
                        .exists())
                .andDo(System.out::println);
    }
}
