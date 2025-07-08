package com.LetMeDoWith.LetMeDoWith.domain.notification.repository;

import com.LetMeDoWith.LetMeDoWith.domain.notification.enums.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;

import java.util.Optional;

public interface NotificationTemplateRepository {
    Optional<NotificationTemplate> getNotificationTemplate(NotificationTemplateCode templateCode);
}
