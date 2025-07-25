package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateJpaRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByCode(String code);
}
