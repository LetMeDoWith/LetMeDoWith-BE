package com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberBadgeVO;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QBadge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMemberBadge;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QBadgeJpaRepositoryImpl implements QBadgeJpaRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private QBadge qBadge = QBadge.badge;
    private QMemberBadge qMemberBadge = QMemberBadge.memberBadge;

    @Override
    public List<MemberBadgeVO> findAllJoinMemberBadge(Long memberId, BadgeStatus badgeStatus) {
        return jpaQueryFactory
                .select(
                        Projections.bean(
                                MemberBadgeVO.class,
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
                .where(qBadge.badgeStatus.eq(badgeStatus))
                .orderBy(qBadge.sortOrder.asc())
                .fetch();
    }
}
