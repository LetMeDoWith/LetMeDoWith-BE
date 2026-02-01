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
