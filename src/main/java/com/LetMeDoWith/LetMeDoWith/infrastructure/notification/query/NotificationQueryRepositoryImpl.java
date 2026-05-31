package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.QNotification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.dto.NotificationQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QNotification notification = QNotification.notification;

    @Override
    public Long countNotifications(String memberId, NotificationType type) {
        return queryFactory
                .select(Wildcard.count)
                .from(notification)
                .where(notification.memberId.eq(memberId).and(notification.type.eq(type)))
                .fetchOne();
    }

    @Override
    public List<NotificationQueryDto> getNotifications(String memberId, NotificationType type, long offset, int limit) {
        return queryFactory
                .select(Projections.constructor(
                        NotificationQueryDto.class,
                        notification.id,
                        notification.title,
                        notification.body,
                        notification.imageUrl,
                        notification.deepLink,
                        notification.isConfirmed,
                        notification.createdAt))
                .from(notification)
                .where(notification.memberId.eq(memberId).and(notification.type.eq(type)))
                .orderBy(notification.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }
}
