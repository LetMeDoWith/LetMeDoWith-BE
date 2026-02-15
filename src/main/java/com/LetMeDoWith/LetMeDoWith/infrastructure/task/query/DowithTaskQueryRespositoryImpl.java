package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.*;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.SuccessDowithTaskQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DowithTaskQueryRespositoryImpl implements DowithTaskQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QDowithTask dowithTask = QDowithTask.dowithTask;
    private final QDowithTaskRoutine dowithTaskRoutine = QDowithTaskRoutine.dowithTaskRoutine;
    private final QDowithTaskSuccess dowithTaskSuccess = QDowithTaskSuccess.dowithTaskSuccess;
    private final QTaskCategory taskCategory = QTaskCategory.taskCategory;
    private final QDowithTaskLike dowithTaskLike = QDowithTaskLike.dowithTaskLike;
    private final QMember member = QMember.member;
    private final QMember member = QMember.member;
    private final QMemberBadge memberBadge = QMemberBadge.memberBadge;
    private final QBadge badge = QBadge.badge;

    @Override
    public List<DowithTaskQueryDto> getDowithTasks(String memberId, LocalDate startDate,
        LocalDate endDate) {
        return queryFactory
            .select(Projections.constructor(
                DowithTaskQueryDto.class,
                dowithTask.id,
                dowithTask.taskCategoryId,
                taskCategory.title,
                dowithTask.title,
                dowithTask.status,
                dowithTask.date,
                dowithTask.startTime,
                dowithTaskSuccess.imageUrl,
                dowithTaskRoutine,
                Expressions.constant(0) // TODO -
                // 추후
                // FeedBack
                // 개발시
                // 추가
            ))
            .from(dowithTask)
            .leftJoin(taskCategory)
            .on(dowithTask.taskCategoryId.eq(taskCategory.id))
            .leftJoin(dowithTaskSuccess)
            .on(dowithTaskSuccess.dowithTask.eq(dowithTask))
            .leftJoin(dowithTaskRoutine)
            .on(dowithTask.routine.id.eq(dowithTaskRoutine.id))
            .where(
                dowithTask.memberId.eq(memberId).and(dowithTask.date.between(startDate, endDate)))
            .fetch();
    }

    @Override
    public Optional<DowithTaskDetailQueryDto> getDowithTask(String memberId, Long dowithTaskId) {
        DowithTaskDetailQueryDto result = queryFactory
            .select(Projections.constructor(
                DowithTaskDetailQueryDto.class,
                dowithTask.id,
                taskCategory.id,
                taskCategory.title,
                dowithTask.title,
                dowithTask.status,
                dowithTask.date,
                dowithTask.startTime,
                dowithTaskSuccess.imageUrl,
                dowithTaskRoutine.id,
                dowithTaskRoutine.rangeStartDate,
                dowithTaskRoutine.rangeEndDate,
                dowithTaskRoutine.cycle,
                dowithTaskRoutine.pattern,
                dowithTaskRoutine.isExcludeHolidays,
                Expressions.constant(0) // TODO -
                // 추후
                // FeedBack
                // 개발시
                // 추가
            ))
            .from(dowithTask)
            .leftJoin(taskCategory)
            .on(dowithTask.taskCategoryId.eq(taskCategory.id))
            .leftJoin(dowithTaskSuccess)
            .on(dowithTaskSuccess.dowithTask.eq(dowithTask))
            .leftJoin(dowithTaskRoutine)
            .on(dowithTask.routine.id.eq(dowithTaskRoutine.id))
            .where(dowithTask.memberId.eq(memberId).and(dowithTask.id.eq(dowithTaskId)))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<SuccessDowithTaskQueryDto> getSuccessDowithTasks(String requestMemberId, int offset, int limit) {
        return queryFactory
                .select(Projections.constructor(
                        SuccessDowithTaskQueryDto.class,
                        dowithTask.id,
                        dowithTask.title,
                        member.nickname,
                        member.profileImageUrl,
                        dowithTaskSuccess.imageUrl,
                        JPAExpressions.selectOne()
                                .from(dowithTaskLike)
                                .where(
                                        dowithTaskLike.dowithTask.eq(dowithTask),
                                        dowithTaskLike.memberId.eq(requestMemberId))
                                .exists()))
                .from(dowithTask)
                .leftJoin(member)
                .on(dowithTask.memberId.eq(member.id))
                .join(dowithTaskSuccess)
                .on(dowithTaskSuccess.dowithTask.eq(dowithTask))
                .where(dowithTask.status.eq(DowithTaskStatus.SUCCESS))
                .orderBy(dowithTaskSuccess.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public Map<Long, Long> countDowithTaskLikes(Set<Long> dowithTaskIds) {
        return queryFactory
                .select(dowithTask.id, dowithTaskLike.count())
                .from(dowithTaskLike)
                .where(dowithTaskLike.dowithTask.id.in(dowithTaskIds))
                .groupBy(dowithTaskLike.dowithTask.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(dowithTask.id), tuple -> Optional.ofNullable(
                                tuple.get(dowithTaskLike.count()))
                        .orElse(0L)));
    }
}

    @Override
    public List<FeedbackAvailableDowithTasksQueryDto> getFeedbackAvailableDowithTasks(Long offset,
        int size) {

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
            condition.and(
                (dowithTask.date.eq(startRangeDate).and(dowithTask.startTime.gt(startRangeTime)))
                    .or(dowithTask.date.eq(today).and(dowithTask.startTime.loe(nowTime))));
        }

        return queryFactory
            .select(Projections.constructor(
                FeedbackAvailableDowithTasksQueryDto.class,
                dowithTask.id,
                dowithTask.memberId,
                member.nickname,
                badge.imageUrl,
                dowithTask.title,
                dowithTask.status.stringValue(),
                dowithTask.date,
                dowithTask.startTime,
                Expressions.constant(0), // 임시값 0 (Integer) 주입
                Expressions.constant(Collections.emptyList()) // 임시 빈 리스트 주입
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
    }
}