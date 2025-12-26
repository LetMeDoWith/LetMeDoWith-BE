package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.notification.client.MessageServerClient;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTemplateRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTokenRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final MessageServerClient messageServerClient;

    private final NotificationRepository notificationRepository;
    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;

    @Async
    @Transactional
    public void sendNotification(
            String memberId, String templateCode, Map<String, String> titleParams, Map<String, String> bodyParams) {

        NotificationToken notificationToken = notificationTokenRepository
                .getNotificationToken(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        if (!notificationToken.isExpired()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);

        NotificationTemplate notificationTemplate = notificationTemplateRepository
                .getNotificationTemplate(templateCode)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        String title = notificationTemplate.parseTitle(titleParams);
        String body = notificationTemplate.parseBody(bodyParams);

        messageServerClient.sendMessage(
                notificationToken.getToken(),
                title,
                body,
                notificationTemplate.getAppDeepLink(),
                () -> this.saveNotification(memberId, title, body, notificationTemplate.getAppDeepLink(), templateCode),
                () -> this.expireToken(memberId));
    }

    @Transactional
    protected void saveNotification(
            String memberId, String title, String body, String deeplink, String notificationTemplateCode) {

        this.notificationRepository.save(Notification.of(memberId, title, body, deeplink, notificationTemplateCode));
    }

    @Transactional
    protected void expireToken(String memberId) {

        NotificationToken notificationToken = notificationTokenRepository
                .getNotificationToken(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        notificationToken.expireToken();
        notificationTokenRepository.save(notificationToken);
    }
}
