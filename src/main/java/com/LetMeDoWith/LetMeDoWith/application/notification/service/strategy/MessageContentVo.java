package com.LetMeDoWith.LetMeDoWith.application.notification.service.strategy;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;

public record MessageContentVo(
        String receiverMemberId,
        String notificationToken,
        String title,
        String body,
        String deeplink,
        String imageUrl,
        NotificationType type) {}
