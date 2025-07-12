package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.application.notification.client.MessageServerClient;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.notification.enums.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTemplateRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MessageServerClient messageServerClient;

    private final NotificationSendCallbackHandler notificationSendCallbackHandler;

    private final NotificationRepository notificationRepository;
    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;

    @Transactional
    @Async
    public void sendNotification(
            String memberId,
            NotificationTemplateCode templateCode,
            Map<String, String> titleParams,
            Map<String, String> bodyParams) {

        NotificationToken notificationToken = notificationTokenRepository.getNotificationToken(memberId).orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        if (!notificationToken.isExpired()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);

        NotificationTemplate notificationTemplate = notificationTemplateRepository.getNotificationTemplate(templateCode).orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        String title = notificationTemplate.parseTitle(titleParams);
        String body = notificationTemplate.parseBody(bodyParams);

        messageServerClient.sendMessage(
                memberId,
                title,
                body,
                notificationTemplate.getAppDeepLink(),
                () -> notificationSendCallbackHandler.saveNotification(memberId, title, body, notificationTemplate.getAppDeepLink(), templateCode),
                () -> notificationSendCallbackHandler.expireToken(memberId)
        );

    }

    @Transactional
    public void confirmNotification(Long notificationId, String memberId) {

        Notification notification = notificationRepository.getNotification(notificationId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        notification.confirm();

    }

}
