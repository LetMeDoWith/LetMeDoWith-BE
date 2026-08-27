package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTemplateRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTemplateJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationTemplateRepositoryImpl implements NotificationTemplateRepository {

    private final NotificationTemplateJpaRepository jpaRepository;

    @Override
    public Optional<NotificationTemplate> getNotificationTemplate(NotificationTemplateCode templateCode) {
        return jpaRepository.findByCode(templateCode);
    }

    @Override
    public List<NotificationTemplate> getNotificationTemplates(NotificationTemplateCode templateCode) {
        return jpaRepository.findAllByCode(templateCode);
    }
}
