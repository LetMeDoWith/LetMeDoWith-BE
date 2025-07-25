package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationTokenService {

    private final NotificationTokenRepository notificationTokenRepository;

    @Transactional
    public void registerToken(String memberId, String token) {

        NotificationToken notificationToken =
                notificationTokenRepository
                        .getNotificationToken(memberId)
                        .orElseGet(() -> NotificationToken.of(memberId, token));

        notificationToken.updateToNewToken(token);
        notificationTokenRepository.save(notificationToken);
    }
}
