package com.LetMeDoWith.LetMeDoWith.presentation.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "FCM 등록 토큰 등록 요청")
public record RegisterNotificationTokenReqDto(
        @Schema(description = "Firebase Cloud Messaging 디바이스 등록 토큰", example = "eK7Hxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                String notificationToken) {}
