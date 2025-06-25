package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.clirent;

import com.LetMeDoWith.LetMeDoWith.application.notification.client.MessageServerClient;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageClient implements MessageServerClient {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendMessage(String token, String title, String body) {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(token)
                .build();
        try {
            String send = firebaseMessaging.send(message);
            System.out.println(send);
        } catch (FirebaseMessagingException e) {
            // 예외 처리 로직 추가
            throw new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR, e);
        }

    }
}
