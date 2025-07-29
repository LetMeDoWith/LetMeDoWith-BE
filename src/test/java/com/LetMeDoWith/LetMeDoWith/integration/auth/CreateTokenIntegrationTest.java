package com.LetMeDoWith.LetMeDoWith.integration.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.OidcPublicKeyResDto;
import com.LetMeDoWith.LetMeDoWith.application.auth.dto.OidcPublicKeyResDto.OidcPublicKeyVO;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.OidcIdTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.repository.RefreshTokenRepository;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.infrastructure.auth.client.KakaoAuthClient;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberSocialAccountJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.TaskSummaryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.integration.AbstractIntegrationTest;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenResDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import reactor.core.publisher.Mono;

public class CreateTokenIntegrationTest extends AbstractIntegrationTest {

    static final String BASE_URL = "/api/v1/auth";
    static final String CREATE_TOKEN_URL = "/token";

    // 테스트용 OIDC ID Token 상수들 (실제 사용 불가능한 Mock 토큰들)
    private static final String EXISTING_NORMAL_USER_SUBJECT = "5be86359073c434bad2da393222dabce";
    private static final String EXISTING_SOCIAL_AUTH_USER_SUBJECT = "7be86359073c434bad2da393222dadef";
    private static final String NEW_USER_SUBJECT = "9be86359073c434bad2da393222dafgh";
    private static final String SUSPENDED_USER_SUBJECT = "1be86359073c434bad2da393222dajkl";

    @MockBean(name = "kakaoAuthClient")
    private KakaoAuthClient kakaoAuthClient;

    @MockBean
    private OidcIdTokenProvider oidcIdTokenProvider;

    @Autowired
    private MemberJpaRepository testMemberJpaRepository;

    @Autowired
    private MemberSocialAccountJpaRepository memberSocialAccountJpaRepository;

    @Autowired
    private TaskSummaryJpaRepository testTaskSummaryJpaRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MockMvc testMockMvc;

    private Member existingNormalMember;
    private Member existingSocialAuthMember;
    private Member suspendedMember;

    @BeforeEach
    void setUpMocks() {
        setupMockBehaviors();
    }

    @Override
    protected void createTestData() {
        // 정상 회원 생성
        existingNormalMember = testMemberJpaRepository.save(Member.builder()
                .subject(EXISTING_NORMAL_USER_SUBJECT)
                .status(MemberStatus.NORMAL)
                .nickname("existingNormalUser")
                .type(MemberType.USER)
                .build());
        memberSocialAccountJpaRepository.save(MemberSocialAccount.of(existingNormalMember, SocialProvider.KAKAO));

        // 소셜 인증 완료 상태 회원 생성
        existingSocialAuthMember = testMemberJpaRepository.save(Member.builder()
                .subject(EXISTING_SOCIAL_AUTH_USER_SUBJECT)
                .status(MemberStatus.SOCIAL_AUTHENTICATED)
                .type(MemberType.USER)
                .build());
        memberSocialAccountJpaRepository.save(MemberSocialAccount.of(existingSocialAuthMember, SocialProvider.KAKAO));

        // 정지된 회원 생성
        suspendedMember = testMemberJpaRepository.save(Member.builder()
                .subject(SUSPENDED_USER_SUBJECT)
                .status(MemberStatus.SUSPENDED)
                .type(MemberType.USER)
                .build());
        memberSocialAccountJpaRepository.save(MemberSocialAccount.of(suspendedMember, SocialProvider.KAKAO));
    }

    private void setupMockBehaviors() {
        // KakaoAuthClient Mock 설정
        OidcPublicKeyResDto mockPublicKeyResponse = new OidcPublicKeyResDto(
                List.of(
                        new OidcPublicKeyVO(
                                "9e2413e3825ac2bab17fe4d4bad9128c",
                                "RSA",
                                "RS256",
                                "sig",
                                "AQAB",
                                "zhMyuF42t7vy2VjnXj2pI2kssakfgaNJqtBqKkh_IBidqKTIM2mEejJ-b0HUwgQ0YzyZGA1OixLxvWuRTrY3j9RXPg0wj7J7e7TkPqZ83sMQ7lUqfzHfR4mMJQ9Si33CFSm8pBkJt38QS9ciLb-uf2cg9N-GSo1e6YAiywlc-w5UOW9Ur_2N5OeHQAWJM1V7LxSbJEakGJG_ivrghrLfh9h-VaYcvfyCJnbkcHGtpubH7LSo5a80_-S9hkvoHuhow27w9mxLm0K4IR1N8BmJbIBc19pMm8i-BQouHL0tbOr0-843GpoidCsXsk-jL9Egqmp9W3qA_WDU6Ra_SFJzFmbC6lqWveUYcKIh7h-qjpkwWrU_88kO5WuX0QiyV4VDj_uRhbtkMxzKWC-QVFGOhG5h2FJnC1lL1lQaIPa5KfxcxpptThLho1NKkgQoblItidMb3rxHdxMrWHVMkvgPhbN2Z5Yb3zo0Yxa9Svbh0n73iTB2GNrdM8q8EC12abHZ")));
        when(kakaoAuthClient.getPublicKeyList()).thenReturn(Mono.just(mockPublicKeyResponse));

        // OidcIdTokenProvider Mock 설정 - 각 subject별로 다른 Claims 반환
        when(oidcIdTokenProvider.getVerifiedOidcIdToken(any(SocialProvider.class), anyString()))
                .thenAnswer(invocation -> {
                    String token = invocation.getArgument(1);

                    // 토큰에 따라 다른 subject 반환하도록 Mock Claims 생성
                    String subject;
                    if (token.contains("existing_normal")) {
                        subject = EXISTING_NORMAL_USER_SUBJECT;
                    } else if (token.contains("existing_social_auth")) {
                        subject = EXISTING_SOCIAL_AUTH_USER_SUBJECT;
                    } else if (token.contains("new_user")) {
                        subject = NEW_USER_SUBJECT;
                    } else if (token.contains("suspended")) {
                        subject = SUSPENDED_USER_SUBJECT;
                    } else {
                        subject = NEW_USER_SUBJECT; // 기본값
                    }

                    // Mock Claims 생성
                    Claims mockClaims = mock(Claims.class);
                    when(mockClaims.get("sub", String.class)).thenReturn(subject);
                    when(mockClaims.getIssuer()).thenReturn("https://kauth.kakao.com");
                    when(mockClaims.getAudience()).thenReturn("letmedowithSampleApplication");
                    when(mockClaims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 3600000));

                    // Mock Jws 생성
                    Jws<Claims> mockJws = mock(Jws.class);
                    when(mockJws.getBody()).thenReturn(mockClaims);
                    when(mockJws.getSignature()).thenReturn("mock_signature");

                    return mockJws;
                });
    }

    @Test
    @DisplayName("[SUCCESS] 최초 소셜 로그인 - 회원가입 필요")
    void createToken_firstTimeLogin_returnsSignupToken() throws Exception {
        // given
        CreateTokenReqDto requestBody = new CreateTokenReqDto(SocialProvider.KAKAO, "new_user_token");

        // when
        ResultActions resultActions = performCreateTokenRequest(requestBody);
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        String responseBody = response.getContentAsString();

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("S101"))
                .andExpect(jsonPath("$.data.signupToken.token").exists())
                .andExpect(jsonPath("$.data.atk").doesNotExist())
                .andExpect(jsonPath("$.data.rtk").doesNotExist())
                .andExpect(jsonPath("$.data.memberId").doesNotExist());

        // DTO 매핑 검증
        CreateTokenResDto responseDto = readResponse(responseBody, CreateTokenResDto.class);
        assertThat(responseDto.signupToken()).isNotNull();
        assertThat(responseDto.signupToken().token()).isNotEmpty();
        assertThat(responseDto.atk()).isNull();
        assertThat(responseDto.rtk()).isNull();
        assertThat(responseDto.memberId()).isNull();

        // 새로운 멤버가 생성되었는지 확인
        assertThat(testMemberJpaRepository.findAll()).hasSize(5); // requestMember + 3개 테스트 멤버 + 새로 생성된 멤버
        // 새로 생성된 TaskSummary 확인
        assertThat(testTaskSummaryJpaRepository.findAll()).hasSize(2); // requestMember TaskSummary + 새로 생성된 것
    }

    @Test
    @DisplayName("[SUCCESS] 소셜 인증 완료, 회원가입 미완료 - 회원가입 토큰 반환")
    void createToken_socialAuthenticatedMember_returnsSignupToken() throws Exception {
        // given
        CreateTokenReqDto requestBody = new CreateTokenReqDto(SocialProvider.KAKAO, "existing_social_auth_token");

        // when
        ResultActions resultActions = performCreateTokenRequest(requestBody);

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("S101"))
                .andExpect(jsonPath("$.data.signupToken.token").exists())
                .andExpect(jsonPath("$.data.atk").doesNotExist())
                .andExpect(jsonPath("$.data.rtk").doesNotExist())
                .andExpect(jsonPath("$.data.memberId").doesNotExist());
    }

    @Test
    @DisplayName("[SUCCESS] 정상 회원 로그인 - 액세스/리프레시 토큰 반환")
    void createToken_normalMember_returnsTokens() throws Exception {
        // given
        CreateTokenReqDto requestBody = new CreateTokenReqDto(SocialProvider.KAKAO, "existing_normal_token");

        // when
        ResultActions resultActions = performCreateTokenRequest(requestBody);

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("S100"))
                .andExpect(jsonPath("$.data.atk.token").exists())
                .andExpect(jsonPath("$.data.rtk.token").exists())
                .andExpect(jsonPath("$.data.memberId").value(existingNormalMember.getId()))
                .andExpect(jsonPath("$.data.signupToken").doesNotExist());
    }

    @Test
    @DisplayName("[FAIL] 정지된 회원 로그인 시도 - 예외 발생")
    void createToken_suspendedMember_throwsException() throws Exception {
        // given
        CreateTokenReqDto requestBody = new CreateTokenReqDto(SocialProvider.KAKAO, "suspended_token");

        // when
        ResultActions resultActions = performCreateTokenRequest(requestBody);

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.statusCode").value(FailResponseStatus.LOGIN_ATTEMPTED_SUSPENED.getStatusCode()));
    }

    @Test
    @DisplayName("[FAIL] 잘못된 OIDC ID Token - 예외 발생")
    void createToken_invalidIdToken_throwsException() throws Exception {
        // given
        // OidcIdTokenProvider Mock을 예외 발생하도록 설정
        when(oidcIdTokenProvider.getVerifiedOidcIdToken(any(SocialProvider.class), anyString()))
                .thenThrow(new com.LetMeDoWith.LetMeDoWith.common.exception.RestApiAuthException(
                        FailResponseStatus.INVALID_TOKEN));

        CreateTokenReqDto requestBody = new CreateTokenReqDto(SocialProvider.KAKAO, "invalid_token");

        // when
        ResultActions resultActions = performCreateTokenRequest(requestBody);

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.statusCode").value(FailResponseStatus.INVALID_TOKEN.getStatusCode()));
    }

    private ResultActions performCreateTokenRequest(CreateTokenReqDto requestBody) throws Exception {
        return testMockMvc
                .perform(MockMvcRequestBuilders.post(BASE_URL + CREATE_TOKEN_URL)
                        .contentType("application/json")
                        .content(writeRequestBodyAsString(requestBody)))
                .andDo(System.out::println);
    }

    @Override
    protected void deleteTestData() {
        try {
            refreshTokenRepository.deleteRefreshTokens(requestMember.getId());
            if (existingNormalMember != null) {
                refreshTokenRepository.deleteRefreshTokens(existingNormalMember.getId());
            }
            if (existingSocialAuthMember != null) {
                refreshTokenRepository.deleteRefreshTokens(existingSocialAuthMember.getId());
            }
            if (suspendedMember != null) {
                refreshTokenRepository.deleteRefreshTokens(suspendedMember.getId());
            }
        } catch (UnsupportedOperationException e) {
            // Redis repository의 deleteByMemberId 메서드가 지원되지 않는 경우 무시
        }
        memberSocialAccountJpaRepository.deleteAll();
        testTaskSummaryJpaRepository.deleteAll();
        testMemberJpaRepository.deleteAll();
    }
}
