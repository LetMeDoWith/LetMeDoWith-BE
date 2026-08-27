package com.LetMeDoWith.LetMeDoWith.domain.notification.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import java.util.List;
import java.util.Optional;

public interface NotificationTemplateRepository {
    Optional<NotificationTemplate> getNotificationTemplate(NotificationTemplateCode templateCode);

    List<NotificationTemplate> getNotificationTemplates(NotificationTemplateCode templateCode);
}
