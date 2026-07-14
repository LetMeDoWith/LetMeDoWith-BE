package com.LetMeDoWith.LetMeDoWith.application.notification.dto.messageServer;

import java.util.Map;
import javax.annotation.Nullable;

public record PushMessageDto(
        String token,
        String title,
        String body,
        String image,
        String appDeepLink,
        @Nullable Map<String, Object> metaData) {

    public PushMessageDto(String token, String title, String body, String image, String appDeepLink) {
        this(token, title, body, image, appDeepLink, null);
    }
}
