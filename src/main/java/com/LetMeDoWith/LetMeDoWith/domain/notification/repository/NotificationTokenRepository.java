package com.LetMeDoWith.LetMeDoWith.domain.notification.repository;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import java.util.Optional;

public interface NotificationTokenRepository {
    Optional<NotificationToken> getNotificationToken(String memberId);

    void save(NotificationToken notificationToken);
}
