package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTokenJpaRepository extends JpaRepository<NotificationToken, Long> {
    Optional<NotificationToken> findByMemberId(String memberId);
}
