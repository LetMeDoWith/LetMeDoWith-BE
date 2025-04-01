package com.LetMeDoWith.LetMeDoWith.presentation.auth.controller;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateRefreshTokenResult;
import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateTokenResult;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.service.CreateTokenService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.SuccessResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.HeaderUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateRefreshTokenResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenRefreshReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenTempReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.auth.dto.CreateTokenTempResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authorization", description = "인증")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final CreateTokenService createTokenService;
    
    private final AccessTokenProvider accessTokenProvider;
    
    @Operation(summary = "토큰 재발급", description = "새로운 AccessToken과 RefreshToken을 발급 받습니다.")
    @ApiSuccessResponse(description = "토큰 재 발급 성공")
    @ApiErrorResponses({
        @ApiErrorResponse(
            status = FailResponseStatus.INVALID_JWT_TOKEN_FORMAT,
            description = "ATK가 올바른 JWT 형식이 아닐 때 발생"),
        @ApiErrorResponse(
            status = FailResponseStatus.TOKEN_EXPIRED_BY_ADMIN,
            description = "ATK가 운영자에 의해 강제로 만료됨. 재시도 필요"),
        @ApiErrorResponse(
            status = FailResponseStatus.INVALID_RTK_TOKEN_MEMBER_NOT_MATCHED,
            description = "RTK의 소유자가 일치하지 않을 때 발생"),
        @ApiErrorResponse(
            status = FailResponseStatus.INVALID_RTK_TOKEN_ATK_NOT_MATCHED,
            description = "서로 다른 시점에 발급돤 RTK와 ATK로 요청했을 때 발생"),
        @ApiErrorResponse(
            status = FailResponseStatus.INVALID_RTK_TOKEN_USER_AGENT_NOT_MATCHED,
            description = "토큰 발급 시 사용된 User-Agent와 다른 User-Agent로 요청했을 때 발생"),
    })
    @PostMapping("/token/refresh")
    public ResponseEntity<ResponseDto<CreateRefreshTokenResDto>> createTokenRefresh(
        @RequestBody CreateTokenRefreshReqDto requestBody) {
        
        String userAgent = HeaderUtil.getUserAgent();
        String accessToken = AuthUtil.getAccessToken();
        
        CreateRefreshTokenResult result = createTokenService.createRefreshToken(accessToken,
                                                                                requestBody.refreshToken(),
                                                                                userAgent);
        
        return ResponseUtil.createSuccessResponse(CreateRefreshTokenResDto.of(result));
    }
    
    @Operation(summary = "토큰 발급", description = "소셜 로그인 idToken을 통해 AccessToken과 RefreshToken을 발급 받습니다. Provider : KAKAO - 카카오 / GOOGLE - 구글 / APPLE - 애플 / DEV_KAKAO - 카카오 (개발서버 전용)")
    @ApiSuccessResponse(description = "토큰발급 성공. 이미 존재하는 회원일 때는 singupToken 필드가, 회원가입이 필요한 경우 atk, rtk 필드가 null로 설정됩니다.")
    @ApiErrorResponses({
        @ApiErrorResponse(
            status = FailResponseStatus.OIDC_ID_TOKEN_PUBKEY_NOT_FOUND,
            description = "OIDC ID Token의 Public Key를 찾을 수 없을 때 발생. Social Provider의 인증서버 문제의 가능성이 높음"),
    })
    @PostMapping("/token")
    public ResponseEntity<ResponseDto<CreateTokenResDto>> createToken(
        @RequestBody CreateTokenReqDto createTokenReqDto) {
        
        CreateTokenResult createTokenResultRequestResult = createTokenService.createToken(
            createTokenReqDto.provider(),
            createTokenReqDto.idToken()
        );
        
        return ResponseUtil.createSuccessResponse(
            createTokenResultRequestResult.signupToken() == null
                ? SuccessResponseStatus.OK
                : SuccessResponseStatus.PROCEED_TO_SIGNUP
            , CreateTokenResDto.fromCreateTokenResult(createTokenResultRequestResult));
    }
    
    @Operation(summary = "개발 환경 임시 토큰 발급", description = "개발 환경용 임시 토큰 발급")
    @ApiSuccessResponse(description = "개발 환경용 토큰 발급 성공")
    @ApiErrorResponses({
        @ApiErrorResponse(
            status = FailResponseStatus.UNAUTHORIZED,
            description = "개발용 인증을 위한 id, pw를 올바르게 입력하지 않았을 때 발생")
    })
    @PostMapping("/token/temp")
    public ResponseEntity<ResponseDto<CreateTokenTempResDto>> createTokenTemp(
        @RequestBody CreateTokenTempReqDto requestBody
    ) {
        String id = "admin0114";
        String password = "dev-letmedowith";
        
        AccessToken accessToken = null;
        if (requestBody.id().equals(id) && requestBody.password().equals(password)) {
            accessToken = accessTokenProvider.createAccessToken(
                requestBody.memberId());
        } else {
            throw new RestApiException(FailResponseStatus.UNAUTHORIZED);
        }
        
        return ResponseUtil.createSuccessResponse(new CreateTokenTempResDto(accessToken.getToken()));
    }
}