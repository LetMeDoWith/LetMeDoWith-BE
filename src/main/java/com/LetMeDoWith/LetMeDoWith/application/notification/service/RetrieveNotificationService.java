package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.application.notification.dto.RetrieveNotificationsResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.dto.NotificationQueryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveNotificationService {

    private final NotificationQueryRepository notificationQueryRepository;

    public RetrieveNotificationsResult retrieveNotifications(
            String memberId, NotificationType type, Pageable pageable) {
        Long totalCount = notificationQueryRepository.countNotifications(memberId, type);
        List<NotificationQueryDto> notifications = notificationQueryRepository.getNotifications(
                memberId, type, pageable.getOffset(), pageable.getPageSize());
        return RetrieveNotificationsResult.from(totalCount, notifications);
    }
}
