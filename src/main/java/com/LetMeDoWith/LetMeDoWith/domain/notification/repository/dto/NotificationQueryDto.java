package com.LetMeDoWith.LetMeDoWith.domain.notification.repository.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import java.time.LocalDateTime;

public record NotificationQueryDto(
        Long id,
        String title,
        String body,
        String imageUrl,
        String deepLink,
        Yn isConfirmed,
        LocalDateTime createdAt) {}
