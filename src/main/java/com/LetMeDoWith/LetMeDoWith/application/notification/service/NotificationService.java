package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void confirmNotification(Long notificationId, String memberId) {
        Notification notification = notificationRepository
                .getNotification(notificationId, memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        notification.confirm();
    }
}
