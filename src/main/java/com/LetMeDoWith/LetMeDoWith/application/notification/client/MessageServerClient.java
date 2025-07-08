package com.LetMeDoWith.LetMeDoWith.application.notification.client;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.function.Consumer;

public interface MessageServerClient {
    void sendMessage(String token, String title, String body, String link, Runnable onSuccess, Consumer<Throwable> onFailure)
            throws FirebaseMessagingException;
}
