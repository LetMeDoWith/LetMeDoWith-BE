package com.LetMeDoWith.LetMeDoWith.presentation.notification.controller;

import com.LetMeDoWith.LetMeDoWith.application.notification.service.NotificationTokenService;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.notification.dto.RegisterNotificationTokenReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification Token", description = "알림 토큰")
@RestController
@RequestMapping("/api/v1/notifications/tokens")
@RequiredArgsConstructor
public class NotificationTokenController {

    private final NotificationTokenService notificationTokenService;

    @Operation(summary = "알림(FCM) 토큰 등록", description = "알림(Firebase Cloud Messaging) 토큰을 등록합니다.")
    @PostMapping("")
    public ResponseEntity registerNotificationToken(
            @RequestBody RegisterNotificationTokenReqDto requestBody) {
        notificationTokenService.registerToken(AuthUtil.getMemberId(), requestBody.notificationToken());
        return ResponseUtil.createSuccessResponse();
    }
}
