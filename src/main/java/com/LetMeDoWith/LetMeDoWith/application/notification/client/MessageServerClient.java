package com.LetMeDoWith.LetMeDoWith.application.notification.client;

public interface MessageServerClient {
    void sendMessage(
            String token,
            String title,
            String body,
            String image,
            String link,
            Runnable onSuccess,
            Runnable onFailureByExpiredToken);
}
