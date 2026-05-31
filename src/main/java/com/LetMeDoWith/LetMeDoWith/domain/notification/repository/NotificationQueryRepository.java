package com.LetMeDoWith.LetMeDoWith.domain.notification.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.dto.NotificationQueryDto;
import java.util.List;

public interface NotificationQueryRepository {

    Long countNotifications(String memberId, NotificationType type);

    List<NotificationQueryDto> getNotifications(String memberId, NotificationType type, long offset, int limit);
}
