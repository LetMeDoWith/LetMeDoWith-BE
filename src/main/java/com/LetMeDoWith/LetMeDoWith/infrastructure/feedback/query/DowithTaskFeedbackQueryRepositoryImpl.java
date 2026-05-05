package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.SortDirection;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QDowithTaskFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.DowithTaskFeedbackQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.*;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DowithTaskFeedbackQueryRepositoryImpl implements DowithTaskFeedbackQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QDowithTaskFeedback dowithTaskFeedback = QDowithTaskFeedback.dowithTaskFeedback;
    private final QMember member = QMember.member;
    private final QDowithTask dowithTask = QDowithTask.dowithTask;

    @Override
    public Long countFeedbacksByTaskId(Long taskId) {
        return queryFactory
                .select(Wildcard.count)
                .from(dowithTaskFeedback)
                .where(dowithTaskFeedback.dowithTaskId.eq(taskId))
                .fetchOne();
    }

    @Override
    public List<DowithTaskFeedbackQueryDto> getFeedbacksByTaskId(Long taskId, Long offset, int size) {
        return queryFactory
                .select(Projections.constructor(
                        DowithTaskFeedbackQueryDto.class,
                        dowithTaskFeedback.id,
                        dowithTaskFeedback.dowithTaskId,
                        dowithTask.title,
                        dowithTaskFeedback.taskFeedbackTemplateId,
                        member.id,
                        member.nickname,
                        member.profileImageUrl,
                        new CaseBuilder()
                                .when(dowithTaskFeedback.isChecked.eq(Yn.TRUE))
                                .then(true)
                                .otherwise(false),
                        dowithTaskFeedback.createdAt))
                .from(dowithTaskFeedback)
                .leftJoin(member)
                .on(dowithTaskFeedback.senderMemberId.eq(member.id))
                .leftJoin(dowithTask)
                .on(dowithTaskFeedback.dowithTaskId.eq(dowithTask.id))
                .where(dowithTaskFeedback.dowithTaskId.eq(taskId))
                .orderBy(dowithTaskFeedback.createdAt.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public Long countFeedbacksBySenderId(String senderId) {
        return queryFactory
                .select(Wildcard.count)
                .from(dowithTaskFeedback)
                .where(dowithTaskFeedback.senderMemberId.eq(senderId))
                .fetchOne();
    }

    @Override
    public List<SentDowithTaskFeedbackQueryDto> getFeedbacksBySenderId(String senderId, Long offset, int size) {
        return queryFactory
                .select(Projections.constructor(
                        SentDowithTaskFeedbackQueryDto.class,
                        dowithTaskFeedback.id,
                        dowithTaskFeedback.dowithTaskId,
                        dowithTask.title,
                        dowithTaskFeedback.taskFeedbackTemplateId,
                        member.id,
                        member.nickname,
                        member.profileImageUrl,
                        new CaseBuilder()
                                .when(dowithTaskFeedback.isChecked.eq(Yn.TRUE))
                                .then(true)
                                .otherwise(false),
                        dowithTask.status))
                .from(dowithTaskFeedback)
                .leftJoin(member)
                .on(dowithTaskFeedback.senderMemberId.eq(member.id))
                .leftJoin(dowithTask)
                .on(dowithTaskFeedback.dowithTaskId.eq(dowithTask.id))
                .where(dowithTaskFeedback.senderMemberId.eq(senderId))
                .orderBy(dowithTaskFeedback.createdAt.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public Long countFeedbacksByReceiverId(String receiverId) {
        return queryFactory
                .select(Wildcard.count)
                .from(dowithTaskFeedback)
                .where(dowithTaskFeedback.receiverMemberId.eq(receiverId))
                .fetchOne();
    }

    @Override
    public List<DowithTaskFeedbackQueryDto> getFeedbacksByReceiverId(String receiverId, Long offset, int size) {
        return queryFactory
                .select(Projections.constructor(
                        DowithTaskFeedbackQueryDto.class,
                        dowithTaskFeedback.id,
                        dowithTaskFeedback.dowithTaskId,
                        dowithTask.title,
                        dowithTaskFeedback.taskFeedbackTemplateId,
                        member.id,
                        member.nickname,
                        member.profileImageUrl,
                        new CaseBuilder()
                                .when(dowithTaskFeedback.isChecked.eq(Yn.TRUE))
                                .then(true)
                                .otherwise(false),
                        dowithTaskFeedback.createdAt))
                .from(dowithTaskFeedback)
                .leftJoin(member)
                .on(dowithTaskFeedback.senderMemberId.eq(member.id))
                .leftJoin(dowithTask)
                .on(dowithTaskFeedback.dowithTaskId.eq(dowithTask.id))
                .where(dowithTaskFeedback.receiverMemberId.eq(receiverId))
                .orderBy(dowithTaskFeedback.createdAt.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public Map<Long, Long> countFeedbacksByTaskIds(List<Long> taskIds) {
        return queryFactory
                .select(dowithTaskFeedback.dowithTaskId, dowithTaskFeedback.count())
                .from(dowithTaskFeedback)
                .where(dowithTaskFeedback.dowithTaskId.in(taskIds))
                .groupBy(dowithTaskFeedback.dowithTaskId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(dowithTaskFeedback.dowithTaskId),
                        tuple -> tuple.get(dowithTaskFeedback.count())));
    }

    @Override
    public Map<Long, List<SentFeedbacksQueryDto>> getSentFeedbacks(String senderId, List<Long> taskIds) {
        Expression<SentFeedbacksQueryDto> feedbackDto = Projections.constructor(
                SentFeedbacksQueryDto.class,
                dowithTaskFeedback.senderMemberId,
                dowithTaskFeedback.taskFeedbackTemplateId,
                dowithTaskFeedback.createdAt);

        List<Tuple> rows = queryFactory
                .select(dowithTaskFeedback.dowithTaskId, feedbackDto)
                .from(dowithTaskFeedback)
                .where(dowithTaskFeedback.dowithTaskId.in(taskIds), dowithTaskFeedback.senderMemberId.eq(senderId))
                .fetch();

        Map<Long, List<SentFeedbacksQueryDto>> result = new HashMap<>();
        for (Tuple row : rows) {
            Long taskId = row.get(dowithTaskFeedback.dowithTaskId);
            SentFeedbacksQueryDto dto = row.get(feedbackDto);
            result.computeIfAbsent(taskId, key -> new ArrayList<>()).add(dto);
        }
        return result;
    }

    @Override
    public List<CountSentFeedback> getCountSentFeedbacks(
            LocalDate rangeStartDate, LocalDate rangeEndDate, SortDirection sortDirection) {

        OrderSpecifier<Long> countOrderSpecifier = sortDirection.equals(SortDirection.ASC)
                ? dowithTaskFeedback.count().asc()
                : dowithTaskFeedback.count().desc();

        return queryFactory
                .select(Projections.constructor(
                        CountSentFeedback.class, dowithTaskFeedback.senderMemberId, dowithTaskFeedback.count()))
                .from(dowithTaskFeedback)
                .where(dowithTaskFeedback.createdAt.between(
                        rangeStartDate.atStartOfDay(), rangeEndDate.plusDays(1).atStartOfDay()))
                .groupBy(dowithTaskFeedback.senderMemberId)
                .orderBy(countOrderSpecifier, dowithTaskFeedback.createdAt.min().asc())
                .fetch();
    }

    @Override
    public List<AggregateTaskFeedbacksQueryDto> aggregateTaskFeedbacks(Long taskId) {

        return queryFactory
                .select(Projections.constructor(
                        AggregateTaskFeedbacksQueryDto.class,
                        dowithTaskFeedback.taskFeedbackTemplateId,
                        dowithTaskFeedback.count()))
                .from(dowithTaskFeedback)
                .where(dowithTaskFeedback.dowithTaskId.eq(taskId))
                .groupBy(dowithTaskFeedback.taskFeedbackTemplateId)
                .fetch();
    }
}
