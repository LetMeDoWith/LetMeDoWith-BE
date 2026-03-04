package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.query;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.JaksimSamilerRankingBatchQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingScoreQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JaksimSamilerRankingBatchQueryRepositoryImpl implements JaksimSamilerRankingBatchQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QDowithTask qDowithTask = QDowithTask.dowithTask;

    @Override
    public List<RankingScoreQueryDto> getRankingScores(
            LocalDateTime aggregationStartDateTime, LocalDateTime aggregationEndDateTime) {
        DateTimeExpression<LocalDateTime> taskStartDateTime = Expressions.dateTimeTemplate(
                LocalDateTime.class, "TIMESTAMP({0}, {1})", qDowithTask.date, qDowithTask.startTime);
        NumberExpression<Long> ranking = Expressions.numberTemplate(
                Long.class,
                "ROW_NUMBER() OVER (ORDER BY {0} DESC, {1} ASC, {2} ASC, {3} ASC)",
                qDowithTask.id.count(),
                taskStartDateTime.max(),
                qDowithTask.createdAt.min(),
                qDowithTask.memberId);

        return queryFactory
                .select(Projections.constructor(
                        RankingScoreQueryDto.class, qDowithTask.memberId, ranking, qDowithTask.id.count()))
                .from(qDowithTask)
                .where(qDowithTask
                        .status
                        .eq(DowithTaskStatus.FAIL)
                        .and(qDowithTask.startTime.isNotNull())
                        .and(taskStartDateTime.goe(aggregationStartDateTime))
                        .and(taskStartDateTime.loe(aggregationEndDateTime)))
                .groupBy(qDowithTask.memberId)
                .orderBy(
                        qDowithTask.id.count().desc(),
                        taskStartDateTime.max().asc(),
                        qDowithTask.createdAt.min().asc(),
                        qDowithTask.memberId.asc())
                .fetch();
    }
}
