package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.persistence.jpaRepository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(notification);
    }

    @Override
    public Optional<Notification> getNotification(Long id) {
        return notificationJpaRepository.findById(id);
    }

    @Override
    public Optional<Notification> getNotification(Long id, String memberId) {
        return notificationJpaRepository.findByIdAndMemberId(id, memberId);
    }
}
