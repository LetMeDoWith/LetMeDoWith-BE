package com.LetMeDoWith.LetMeDoWith.application.auth.service;

import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateRefreshTokenResult;
import com.LetMeDoWith.LetMeDoWith.application.auth.dto.CreateTokenResult;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.AccessTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.OidcIdTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.RefreshTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.provider.SignupTokenProvider;
import com.LetMeDoWith.LetMeDoWith.application.auth.repository.RefreshTokenRepository;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.util.HeaderUtil;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.domain.member.service.SocialAuthMemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTokenService {
    
    private final AccessTokenProvider accessTokenProvider;
    private final SignupTokenProvider signupTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    private final MemberRepository memberRepository;
    private final SocialAuthMemberService socialAuthMemberService;
    
    private final OidcIdTokenProvider oidcIdTokenProvider;
    
    @Transactional
    public CreateTokenResult createToken(Member member) {
        if (member.isNormal()) {
            AccessToken accessToken = accessTokenProvider.createAccessToken(member.getId());
            RefreshToken refreshToken =
                refreshTokenProvider.createRefreshToken(
                    member.getId(), accessToken.getToken(), HeaderUtil.getUserAgent());
            
            return CreateTokenResult.of(accessToken, refreshToken);
        } else {
            throw new RestApiException(member.getStatus().getApiResponseStatus());
        }
    }
    
    /**
     * 회원가입 여부를 판단하여 ATK를 발급한다.
     *
     * <p>입력으로 받은 DTO의 정보를 통해 기 가입 여부를 판단하여, 이미 가입된 유저가 발급을 요청하는 경우 ATK를 발급하고, 아닌 경우 회원가입 프로세스를 진행한다.
     *
     * @param socialProvider
     * @param idToken
     * @return 기 가입되어 있는 경우 ATK, 아닌 경우 회원가입 프로세스로 fallback.
     */
    @Transactional
    public CreateTokenResult createToken(SocialProvider socialProvider, String idToken) {
        Jws<Claims> verifiedIdToken =
            oidcIdTokenProvider.getVerifiedOidcIdToken(socialProvider, idToken);
        
        Claims body = verifiedIdToken.getBody();
        String subject = body.get("sub", String.class);
        
        Optional<Member> optionalMember = memberRepository.getMember(socialProvider, subject);
        
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (member.isNormal()) {
                return createToken(member);
            } else if (member.isSocialAuthenticated()) {
                // 기 가입된 유저가 있으면, 로그인(액세스 토큰을 발급)한다.
                return CreateTokenResult.of(signupTokenProvider.createSignupToken(member.getId()));
            } else {
                throw new RestApiException(member.getStatus().getApiResponseStatus());
            }
        } else {
            // 가입된 유저가 없으면, 회원가입 프로세스를 진행한다.
            // 최초 소셜 로그인 시도시 회원가입 단계를 진행하기 위해 임시 Member를 생성.
            Member member =
                socialAuthMemberService.createSocialAuthenticatedMember(
                    socialProvider, subject, memberRepository);
            
            return CreateTokenResult.of(signupTokenProvider.createSignupToken(member.getId()));
        }
    }
    
    /**
     * Refresh 토큰 생성
     *
     * @param accessToken
     * @param refreshToken
     * @param userAgent
     * @return
     */
    @Transactional
    public CreateRefreshTokenResult createRefreshToken(
        String accessToken, String refreshToken, String userAgent) {
        
        String memberId = accessTokenProvider.getMemberIdWithoutVerify(accessToken);
        
        RefreshToken savedRefreshToken = null;
        savedRefreshToken = refreshTokenProvider.findRefreshToken(refreshToken);
        savedRefreshToken.checkTokenOwnership(memberId, accessToken, userAgent);
        
        AccessToken newAccessToken = accessTokenProvider.createAccessToken(memberId);
        RefreshToken newRefreshToken =
            refreshTokenProvider.createRefreshToken(memberId, accessToken, userAgent);
        
        refreshTokenRepository.deleteRefreshToken(savedRefreshToken);
        
        return new CreateRefreshTokenResult(newAccessToken, newRefreshToken);
    }
}