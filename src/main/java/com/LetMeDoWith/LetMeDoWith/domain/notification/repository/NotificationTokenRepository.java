package com.LetMeDoWith.LetMeDoWith.domain.notification.repository;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NotificationTokenRepository {
    Optional<NotificationToken> getNotificationToken(String memberId);

    List<NotificationToken> getActiveNotificationTokens(Set<String> memberIdSet);

    void save(NotificationToken notificationToken);
}
