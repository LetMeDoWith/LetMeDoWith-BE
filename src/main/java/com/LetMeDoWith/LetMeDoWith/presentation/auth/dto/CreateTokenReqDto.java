package com.LetMeDoWith.LetMeDoWith.presentation.auth.dto;

import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "token 발급 요청")
public record CreateTokenReqDto(
        @Schema(description = "소셜 공급자", implementation = SocialProvider.class, example = "KAKAO")
                SocialProvider provider,
        @Schema(description = "자격증명 제공자로부터 발급받은 OIDC Id Token", example = "eyJhbGciOiJSUzI1NiJ9...") String idToken) {}
