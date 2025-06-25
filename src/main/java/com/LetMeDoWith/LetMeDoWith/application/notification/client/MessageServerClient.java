package com.LetMeDoWith.LetMeDoWith.application.notification.client;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface MessageServerClient {
    void sendMessage(String token, String title, String body) throws FirebaseMessagingException;
}
