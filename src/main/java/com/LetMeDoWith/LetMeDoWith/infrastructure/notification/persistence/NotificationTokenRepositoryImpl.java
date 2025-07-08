package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTokenRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationTokenJpaRepository;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationTokenRepositoryImpl implements NotificationTokenRepository {

    private final NotificationTokenJpaRepository jpaRepository;

    @Override
    public Optional<NotificationToken> getNotificationToken(String memberId) {
        return jpaRepository.findByMemberId(memberId);
    }

    @Override
    public void save(NotificationToken notificationToken) {
        jpaRepository.save(notificationToken);
    }
}
