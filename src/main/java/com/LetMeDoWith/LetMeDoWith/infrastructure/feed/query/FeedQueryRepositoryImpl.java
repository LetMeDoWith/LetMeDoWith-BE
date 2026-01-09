package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QDowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QBadge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMemberBadge;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedQueryRepositoryImpl implements FeedQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QDowithTask dowithTask = QDowithTask.dowithTask;
    private final QDowithTaskFeedback dowithTaskFeedback = QDowithTaskFeedback.dowithTaskFeedback;
    private final QMember member = QMember.member;
    private final QMemberBadge memberBadge = QMemberBadge.memberBadge;
    private final QBadge badge = QBadge.badge;

    @Override
    public List<FeedbackAvailableDowithTaskQueryDto> getFeedbackAvailableDowithTasks(
        LocalDateTime referenceDateTime) {

        LocalDateTime now = referenceDateTime != null ? referenceDateTime : SystemTimeUtil.now();

        LocalDate today = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();

        LocalDateTime startRangeDateTime = now.minusMinutes(59).minusSeconds(59);
        LocalDate startRangeDate = startRangeDateTime.toLocalDate();
        LocalTime startRangeTime = startRangeDateTime.toLocalTime();

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(dowithTask.status.eq(DowithTaskStatus.WAIT));

        if (today.equals(startRangeDate)) {
            // Case 1: 범위 시작과 끝이 같은 날짜인 경우
            condition.and(dowithTask
                .date
                .eq(today)
                .and(dowithTask.startTime.gt(startRangeTime))
                .and(dowithTask.startTime.loe(nowTime)));
        } else {
            // Case 2: 날짜가 걸쳐있는 경우 (자정 직후)
            condition.and(
                (dowithTask.date.eq(startRangeDate).and(dowithTask.startTime.gt(startRangeTime)))
                    .or(dowithTask.date.eq(today).and(dowithTask.startTime.loe(nowTime))));
        }

        return queryFactory
            .select(Projections.constructor(
                FeedbackAvailableDowithTaskQueryDto.class,
                dowithTask.id,
                dowithTask.memberId,
                member.nickname,
                badge.imageUrl,
                dowithTask.title,
                dowithTask.status.stringValue(),
                dowithTask.date,
                dowithTask.startTime,
                JPAExpressions.select(dowithTaskFeedback.count())
                    .from(dowithTaskFeedback)
                    .where(dowithTaskFeedback.dowithTaskId.eq(dowithTask.id))))
            .from(dowithTask)
            .leftJoin(member)
            .on(member.id.eq(dowithTask.memberId))
            .leftJoin(memberBadge)
            .on(memberBadge.memberId.eq(member.id).and(memberBadge.isMain.eq(Yn.TRUE)))
            .leftJoin(badge)
            .on(badge.id.eq(memberBadge.badge.id))
            .where(condition)
            .fetch();
    }
}