package com.LetMeDoWith.LetMeDoWith.domain.notification.repository;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;

import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> getNotification(Long id);

    Optional<Notification> getNotification(Long id, String memberId);
}
