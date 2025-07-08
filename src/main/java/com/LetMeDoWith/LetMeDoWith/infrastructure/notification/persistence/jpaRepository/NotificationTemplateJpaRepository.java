package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.notification.enums.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateJpaRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByCode(NotificationTemplateCode code);
}
