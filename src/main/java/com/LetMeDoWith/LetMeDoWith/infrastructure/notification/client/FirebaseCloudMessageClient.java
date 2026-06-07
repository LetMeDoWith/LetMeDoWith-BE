package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.client;

import com.LetMeDoWith.LetMeDoWith.application.notification.client.MessageServerClient;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseCloudMessageClient implements MessageServerClient {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendMessage(
            String token,
            String title,
            String body,
            String image,
            String appDeepLink,
            Runnable onSuccess,
            Runnable onFailureByExpiredToken) {
        Notification.Builder notificationBuilder =
                Notification.builder().setTitle(title).setBody(body);
        if (image != null && !image.isBlank()) {
            notificationBuilder.setImage(image);
        }

        Message message = Message.builder()
                .setNotification(notificationBuilder.build())
                .putData("deepLink", appDeepLink)
                .setToken(token)
                .build();
        try {
            String send = firebaseMessaging.send(message);
            log.info("Firebase Cloud Messaging sent successfully: {}", send);
            onSuccess.run();
        } catch (FirebaseMessagingException e) {
            // 예외 처리 로직 추가
            if (e.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                onFailureByExpiredToken.run();
            }
            throw new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
