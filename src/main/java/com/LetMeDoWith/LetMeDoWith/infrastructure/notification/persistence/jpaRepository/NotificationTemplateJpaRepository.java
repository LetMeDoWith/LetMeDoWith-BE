package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateJpaRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByCode(NotificationTemplateCode code);

    List<NotificationTemplate> findAllByCode(NotificationTemplateCode code);
}
