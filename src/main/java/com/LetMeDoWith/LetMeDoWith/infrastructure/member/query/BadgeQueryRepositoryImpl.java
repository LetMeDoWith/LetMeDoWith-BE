package com.LetMeDoWith.LetMeDoWith.infrastructure.member.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QBadge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMemberBadge;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.query.dto.MemberBadgeQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BadgeQueryRepositoryImpl implements BadgeQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QBadge qBadge = QBadge.badge;
    private final QMemberBadge qMemberBadge = QMemberBadge.memberBadge;

    @Override
    public List<MemberBadgeQueryDto> getBadges(Long memberId) {

        return jpaQueryFactory
                .select(
                        Projections.bean(
                                MemberBadgeQueryDto.class,
                                qMemberBadge.id.as("memberBadgeId"),
                                qMemberBadge.memberId,
                                qMemberBadge.isMain,
                                qBadge.id.as("badgeId"),
                                qBadge.badgeStatus,
                                qBadge.name,
                                qBadge.description,
                                qBadge.acquireHint,
                                qBadge.imageUrl,
                                qBadge.sortOrder))
                .from(qBadge)
                .leftJoin(qBadge.memberBadges, qMemberBadge)
                .on(qMemberBadge.memberId.eq(memberId))
                .where(qBadge.badgeStatus.eq(BadgeStatus.ACTIVE))
                .orderBy(qBadge.sortOrder.asc())
                .fetch();
    }
}
