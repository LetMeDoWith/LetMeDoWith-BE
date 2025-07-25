package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    // TODO - 알림 기획 확인 후 구체화
    // @Transactional
    // pub

    // @Transactional
    // public void confirmNotification(Long notificationId, String memberId) {
    //
    // Notification notification =
    // notificationRepository.getNotification(notificationId,
    // memberId)
    // .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
    //
    // notification.confirm();
    //
    // }

}
