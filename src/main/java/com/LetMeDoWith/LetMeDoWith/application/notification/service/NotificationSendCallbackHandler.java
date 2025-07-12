package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.notification.enums.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSendCallbackHandler {

    private final NotificationRepository notificationRepository;
    private final NotificationTokenRepository notificationTokenRepository;

    @Transactional
    public void saveNotification(String memberId, String title, String body, String deeplink, NotificationTemplateCode notificationTemplateCode) {

        this.notificationRepository.save(Notification.of(
                memberId,
                title,
                body,
                deeplink,
                notificationTemplateCode
        ));

    }

    @Transactional
    protected void expireToken(String memberId) {

        NotificationToken notificationToken = notificationTokenRepository.getNotificationToken(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        notificationToken.expireToken();
        notificationTokenRepository.save(notificationToken);

    }
}
