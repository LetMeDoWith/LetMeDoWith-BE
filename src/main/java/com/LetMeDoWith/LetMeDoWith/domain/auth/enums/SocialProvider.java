package com.LetMeDoWith.LetMeDoWith.domain.auth.enums;

import com.LetMeDoWith.LetMeDoWith.common.converter.member.SocialProviderConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = SocialProviderConverter.class)
@Schema(enumAsRef = true)
public enum SocialProvider implements BaseEnum {
    
    KAKAO("KAKAO", "카카오", "https://kauth.kakao.com"),
    GOOGLE("GOOGLE", "구글", "https://accounts.google.com"),
    APPLE("APPLE", "애플", ""),
    
    DEV_KAKAO("DEV_KAKAO", "카카오 (개발서버)", "https://kauth.kakao.com");
    
    private final String code;
    private final String description;
    // 자격증명 제공자 검증을 위한 필드
    private final String issUrl;
    
    
    /**
     * OIDC id 토큰의 iss 필드 값으로 social provider 조회
     *
     * @param issUrl id 토큰 내 iss 필드 값
     * @return 자격 증명 제공자의 social provider 타입
     */
    public SocialProvider fromIssUrl(String issUrl) {
        return Arrays.stream(SocialProvider.values())
                     .filter(socialProvider -> socialProvider.getIssUrl().equals(issUrl))
                     .findFirst()
                     .orElseThrow(() -> new RestApiException(FailResponseStatus.MANDATORY_PARAM_ERROR_PROVIDER));
    }
    
}