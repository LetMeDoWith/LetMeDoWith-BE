package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;

import com.LetMeDoWith.LetMeDoWith.application.feed.repository.FeedQueryRepository;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QDowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QBadge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMemberBadge;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public List<FeedDowithTaskQueryDto> getFeedbackAvailableDowithTasks(
                String memberId, Long offset, int size) {

        LocalDateTime now = SystemTimeUtil.now();

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
            condition.and((dowithTask.date.eq(startRangeDate).and(dowithTask.startTime.gt(startRangeTime)))
                    .or(dowithTask.date.eq(today).and(dowithTask.startTime.loe(nowTime))));
        }

        // 1. 메인 리스트 조회 (Count는 0으로 임시 설정)
        List<FeedDowithTaskQueryDto> rawTasks = queryFactory
                .select(Projections.constructor(
                        FeedDowithTaskQueryDto.class,
                        dowithTask.id,
                        dowithTask.memberId,
                        member.nickname,
                        badge.imageUrl,
                        dowithTask.title,
                        dowithTask.status.stringValue(),
                        dowithTask.date,
                        dowithTask.startTime,
                        Expressions.asNumber(0L), // 임시값 0L 주입
                        Expressions.asBoolean(false) // 임시값 false 주입
                        ))
                .from(dowithTask)
                .leftJoin(member)
                .on(member.id.eq(dowithTask.memberId))
                .leftJoin(memberBadge)
                .on(memberBadge.memberId.eq(member.id).and(memberBadge.isMain.eq(Yn.TRUE)))
                .leftJoin(badge)
                .on(badge.id.eq(memberBadge.badge.id))
                .where(condition)
                .offset(offset)
                .limit(size)
                .fetch();

        if (rawTasks.isEmpty()) {
            return rawTasks;
        }

        // 2. 조회된 Task ID 추출
        List<Long> taskIds = rawTasks.stream().map(FeedDowithTaskQueryDto::id).toList();

        // 3. transform을 사용하여 Task별 피드백 작성자 ID 목록(Set)을 Map으로 한 번에 조회
        Map<Long, Set<String>> feedbackSendersMap = queryFactory
            .from(dowithTaskFeedback)
            .where(dowithTaskFeedback.dowithTaskId.in(taskIds))
            .transform(
                groupBy(dowithTaskFeedback.dowithTaskId)
                    .as(set(dowithTaskFeedback.senderMemberId))
            );

        // 4. 메모리에서 데이터 조합 (Record 재생성)
        return rawTasks.stream()
            .map(task -> {
                Set<String> senders = feedbackSendersMap.getOrDefault(task.id(),
                    Collections.emptySet());

                return new FeedDowithTaskQueryDto(
                    task.id(),
                    task.memberId(),
                    task.nickname(),
                    task.badgeImageUrl(),
                    task.title(),
                    task.status(),
                    task.date(),
                    task.startTime(),
                    senders.size(),
                    senders.contains(memberId)
                );
            })
            .toList();
    }
}
