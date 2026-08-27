package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.client;

import com.LetMeDoWith.LetMeDoWith.application.notification.client.MessageServerClient;
import com.LetMeDoWith.LetMeDoWith.application.notification.dto.messageServer.PushMessageDto;
import com.LetMeDoWith.LetMeDoWith.application.notification.dto.messageServer.SendMessageResult;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.google.firebase.messaging.*;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public SendMessageResult sendMessages(List<PushMessageDto> messages) {

        try {
            List<SendMessageResult.FailMessage> failMessages = new ArrayList<>();
            BatchResponse batchResponse = firebaseMessaging.sendEach(messages.stream()
                    .map(messageDto -> {
                        Notification.Builder notificationBuilder = Notification.builder()
                                .setTitle(messageDto.title())
                                .setBody(messageDto.body());
                        if (messageDto.image() != null && !messageDto.image().isBlank()) {
                            notificationBuilder.setImage(messageDto.image());
                        }

                        return Message.builder()
                                .setNotification(notificationBuilder.build())
                                .putData("deepLink", messageDto.appDeepLink())
                                .setToken(messageDto.token())
                                .build();
                    })
                    .toList());

            List<SendResponse> responses = batchResponse.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                SendResponse response = responses.get(i);
                if (!response.isSuccessful()) {
                    failMessages.add(new SendMessageResult.FailMessage(
                            response.getMessageId(), messages.get(i).metaData(), response.getException()));
                }
            }

            return new SendMessageResult(
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount(), failMessages);
        } catch (FirebaseMessagingException e) {

            throw new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
