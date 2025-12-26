package com.LetMeDoWith.LetMeDoWith.domain.notification.client;

public interface MessageServerClient {
    void sendMessage(
            String token, String title, String body, String link, Runnable onSuccess, Runnable onFailureByExpiredToken);
}
