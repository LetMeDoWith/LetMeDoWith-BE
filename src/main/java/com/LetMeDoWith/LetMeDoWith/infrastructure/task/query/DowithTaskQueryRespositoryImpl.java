package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QDowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.*;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
    private final QDowithTaskFeedback dowithTaskFeedback = QDowithTaskFeedback.dowithTaskFeedback;
    private final QTaskCategory taskCategory = QTaskCategory.taskCategory;
    private final QDowithTaskLike dowithTaskLike = QDowithTaskLike.dowithTaskLike;
    private final QMember member = QMember.member;

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }
        throw new IllegalArgumentException("Unsupported type: " + value.getClass());
    }

    @Override
    public List<DowithTaskQueryDto> getDowithTasks(String memberId, LocalDate startDate, LocalDate endDate) {
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
                        ExpressionUtils.as(
                                JPAExpressions.select(dowithTaskFeedback.count())
                                        .from(dowithTaskFeedback)
                                        .where(dowithTaskFeedback.dowithTaskId.eq(dowithTask.id)),
                                "feedBackCount")))
                .from(dowithTask)
                .leftJoin(taskCategory)
                .on(dowithTask.taskCategoryId.eq(taskCategory.id))
                .leftJoin(dowithTaskSuccess)
                .on(dowithTaskSuccess.dowithTask.eq(dowithTask))
                .leftJoin(dowithTaskRoutine)
                .on(dowithTask.routine.id.eq(dowithTaskRoutine.id))
                .where(dowithTask.memberId.eq(memberId).and(dowithTask.date.between(startDate, endDate)))
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
                        ExpressionUtils.as(
                                JPAExpressions.select(dowithTaskFeedback.count())
                                        .from(dowithTaskFeedback)
                                        .where(dowithTaskFeedback.dowithTaskId.eq(dowithTask.id)),
                                "feedBackCount")))
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
        if (dowithTaskIds == null || dowithTaskIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return queryFactory
                .select(dowithTaskLike.dowithTask.id, dowithTaskLike.count())
                .from(dowithTaskLike)
                .join(member)
                .on(member.id.eq(dowithTaskLike.memberId))
                .where(dowithTaskLike.dowithTask.id.in(dowithTaskIds), dowithTaskLikerMemberIsNormal())
                .groupBy(dowithTaskLike.dowithTask.id)
                .fetch()
                .stream()
                .collect(
                        Collectors.toMap(tuple -> tuple.get(dowithTaskLike.dowithTask.id), tuple -> Optional.ofNullable(
                                        tuple.get(dowithTaskLike.count()))
                                .orElse(0L)));
    }

    @Override
    public long countDowithTaskLikes(Long dowithTaskId) {
        Long cnt = queryFactory
                .select(dowithTaskLike.count())
                .from(dowithTaskLike)
                .join(member)
                .on(member.id.eq(dowithTaskLike.memberId))
                .where(dowithTaskLike.dowithTask.id.eq(dowithTaskId), dowithTaskLikerMemberIsNormal())
                .fetchOne();
        return cnt != null ? cnt : 0L;
    }

    @Override
    public List<DowithTaskLikeMemberQueryDto> getDowithTaskLikers(Long dowithTaskId, int offset, int limit) {
        return queryFactory
                .select(Projections.constructor(
                        DowithTaskLikeMemberQueryDto.class,
                        dowithTaskLike.id,
                        dowithTaskLike.memberId,
                        member.nickname,
                        member.profileImageUrl))
                .from(dowithTaskLike)
                .join(member)
                .on(member.id.eq(dowithTaskLike.memberId))
                .where(dowithTaskLike.dowithTask.id.eq(dowithTaskId), dowithTaskLikerMemberIsNormal())
                .orderBy(dowithTaskLike.id.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    private BooleanExpression dowithTaskLikerMemberIsNormal() {
        return member.status.eq(MemberStatus.NORMAL);
    }

    @Override
    public List<FeedbackAvailableDowithTasksQueryDto> getFeedbackAvailableDowithTasks(
            Long offset, int size, String excludeMemberId) {
        return queryFactory
                .select(Projections.constructor(
                        FeedbackAvailableDowithTasksQueryDto.class,
                        dowithTask.id,
                        dowithTask.memberId,
                        member.nickname,
                        member.profileImageUrl,
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
                .where(buildFeedbackAvailableCondition(), dowithTask.memberId.ne(excludeMemberId))
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public Long countFeedbackAvailableDowithTasks(String excludeMemberId) {
        return queryFactory
                .select(dowithTask.count())
                .from(dowithTask)
                .where(buildFeedbackAvailableCondition(), dowithTask.memberId.ne(excludeMemberId))
                .fetchOne();
    }

    @Override
    public List<FailedDowithTaskCountQueryDto> getFailedTaskCountsByMember(
            LocalDateTime aggregationStartDateTime, LocalDateTime aggregationEndDateTime) {
        // DATE + TIME 조합: MySQL DATETIME 반환. Connector/J 8.0.23+ 에서는 getObject() 시 LocalDateTime 반환.
        DateTimeExpression<LocalDateTime> taskStartDateTime = Expressions.dateTimeTemplate(
                LocalDateTime.class, "TIMESTAMP({0}, {1})", dowithTask.date, dowithTask.startTime);

        List<Tuple> rows = queryFactory
                .select(dowithTask.memberId, dowithTask.id.count(), taskStartDateTime.max(), dowithTask.createdAt.min())
                .from(dowithTask)
                .where(dowithTask
                        .status
                        .eq(DowithTaskStatus.FAIL)
                        .and(dowithTask.startTime.isNotNull())
                        .and(taskStartDateTime.goe(aggregationStartDateTime))
                        .and(taskStartDateTime.loe(aggregationEndDateTime)))
                .groupBy(dowithTask.memberId)
                .orderBy(
                        dowithTask.id.count().desc(),
                        taskStartDateTime.max().asc(),
                        dowithTask.createdAt.min().asc(),
                        dowithTask.memberId.asc())
                .fetch();

        return rows.stream()
                .map(tuple -> new FailedDowithTaskCountQueryDto(
                        tuple.get(dowithTask.memberId),
                        tuple.get(dowithTask.id.count()),
                        toLocalDateTime(tuple.get(taskStartDateTime.max())),
                        tuple.get(dowithTask.createdAt.min())))
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberTaskSuccessStatsQueryDto> getTaskSuccessStatsByMember(
            LocalDateTime aggregationStartDateTime, LocalDateTime aggregationEndDateTime) {
        DateTimeExpression<LocalDateTime> taskStartDateTime = Expressions.dateTimeTemplate(
                LocalDateTime.class, "TIMESTAMP({0}, {1})", dowithTask.date, dowithTask.startTime);

        NumberExpression<Long> registeredTaskCount = dowithTask.id.count();
        NumberExpression<Long> successTaskCount = Expressions.numberTemplate(
                Long.class,
                "sum(case when {0} = {1} then 1 else 0 end)",
                dowithTask.status.stringValue(),
                Expressions.constant(DowithTaskStatus.SUCCESS.code));

        return queryFactory
                .select(Projections.constructor(
                        MemberTaskSuccessStatsQueryDto.class,
                        dowithTask.memberId,
                        registeredTaskCount,
                        successTaskCount))
                .from(dowithTask)
                .where(taskStartDateTime
                        .goe(aggregationStartDateTime)
                        .and(taskStartDateTime.loe(aggregationEndDateTime)))
                .groupBy(dowithTask.memberId)
                .having(registeredTaskCount.goe(5L))
                .fetch();
    }

    private BooleanBuilder buildFeedbackAvailableCondition() {
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

        return condition;
    }
}
