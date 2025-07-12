package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndMemberId(Long id, String memberId);
}
