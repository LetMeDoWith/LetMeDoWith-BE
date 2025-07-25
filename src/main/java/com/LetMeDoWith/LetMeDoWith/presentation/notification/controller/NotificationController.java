package com.LetMeDoWith.LetMeDoWith.presentation.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification Token", description = "알림 토큰")
@RestController
@RequestMapping("/api/v1/notifications/tokens")
@RequiredArgsConstructor
public class NotificationController {}
