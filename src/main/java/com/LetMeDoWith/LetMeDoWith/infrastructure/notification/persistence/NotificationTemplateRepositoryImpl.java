package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.notification.enums.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTemplateRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTemplateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationTemplateRepositoryImpl implements NotificationTemplateRepository {

    private final NotificationTemplateJpaRepository jpaRepository;

    @Override
    public Optional<NotificationTemplate> getNotificationTemplate(NotificationTemplateCode templateCode) {
        return jpaRepository.findByCode(templateCode);
    }
}
