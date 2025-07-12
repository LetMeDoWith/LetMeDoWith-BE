package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.client;

import com.LetMeDoWith.LetMeDoWith.application.notification.client.MessageServerClient;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageClient implements MessageServerClient {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendMessage(String token, String title, String body, String appDeepLink,
                            Runnable onSuccess, Runnable onFailureByToken) {
        Message message =
                Message.builder()
                        .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                        .putData("deepLink", appDeepLink)
                        .setToken(token)
                        .build();
        try {
            String send = firebaseMessaging.send(message);
            System.out.println(send);
            onSuccess.run();
        } catch (FirebaseMessagingException e) {
            // 예외 처리 로직 추가
            if (e.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                onFailureByToken.run();
            }
            throw new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
