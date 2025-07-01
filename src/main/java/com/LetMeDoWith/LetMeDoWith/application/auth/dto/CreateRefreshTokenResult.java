package com.LetMeDoWith.LetMeDoWith.application.auth.dto;

import com.LetMeDoWith.LetMeDoWith.domain.auth.model.AccessToken;
import com.LetMeDoWith.LetMeDoWith.domain.auth.model.RefreshToken;

public record CreateRefreshTokenResult(
        String memberId, AccessToken accessToken, RefreshToken refreshToken) {}
