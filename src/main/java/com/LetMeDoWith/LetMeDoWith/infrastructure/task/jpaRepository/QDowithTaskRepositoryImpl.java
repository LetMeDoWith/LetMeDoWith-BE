package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTaskConfirm;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTaskRoutine;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QDowithTaskRepositoryImpl implements QDowithTaskRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QDowithTask qDowithTask = QDowithTask.dowithTask;
    private final QDowithTaskConfirm qDowithTaskConfirm = QDowithTaskConfirm.dowithTaskConfirm;
    private final QDowithTaskRoutine qDowithTaskRoutine = QDowithTaskRoutine.dowithTaskRoutine;

    @Override
    public Optional<DowithTask> findDowithTaskAggregate(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qDowithTask)
                        .leftJoin(qDowithTask.confirms, qDowithTaskConfirm)
                        .leftJoin(qDowithTask.routine, qDowithTaskRoutine)
                        .where(qDowithTask.id.eq(id))
                        .fetchJoin()
                        .fetchOne());
    }

    @Override
    public List<DowithTask> findAllDowithTaskAggregates(Long memberId, LocalDate date) {
        Date targetDate = Date.valueOf(date);
        return jpaQueryFactory
                .selectFrom(qDowithTask)
                .leftJoin(qDowithTask.confirms, qDowithTaskConfirm)
                .leftJoin(qDowithTask.routine, qDowithTaskRoutine)
                .where(
                        Expressions.dateTemplate(java.sql.Date.class, "DATE({0})", qDowithTask.date)
                                .eq(targetDate)
                                .and(qDowithTask.memberId.eq(memberId)))
                .orderBy(qDowithTask.createdAt.asc())
                .fetchJoin()
                .fetch();
    }

    @Override
    public List<DowithTask> findAllDowithTaskAggregates(Long memberId, Set<LocalDate> dates) {
        List<Date> targetDates = dates.stream().map(Date::valueOf).toList();
        return jpaQueryFactory
                .selectFrom(qDowithTask)
                .leftJoin(qDowithTask.confirms, qDowithTaskConfirm)
                .leftJoin(qDowithTask.routine, qDowithTaskRoutine)
                .where(
                        Expressions.dateTemplate(java.sql.Date.class, "DATE({0})", qDowithTask.date)
                                .in(targetDates)
                                .and(qDowithTask.memberId.eq(memberId)))
                .orderBy(qDowithTask.createdAt.asc())
                .fetchJoin()
                .fetch();
    }

    @Override
    public Optional<DowithTask> findDowithTaskAggregate(Long id, Long memberId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qDowithTask)
                        .leftJoin(qDowithTask.confirms, qDowithTaskConfirm)
                        .leftJoin(qDowithTask.routine, qDowithTaskRoutine)
                        .where(qDowithTask.id.eq(id).and(qDowithTask.memberId.eq(memberId)))
                        .fetchJoin()
                        .fetchOne());
    }

    @Override
    public List<DowithTask> findAllDowithTaskAggregates(DowithTaskRoutine dowithTaskRoutine) {
        return jpaQueryFactory
                .selectFrom(qDowithTask)
                .leftJoin(qDowithTask.confirms, qDowithTaskConfirm)
                .leftJoin(qDowithTask.routine, qDowithTaskRoutine)
                .where(qDowithTask.routine.eq(dowithTaskRoutine))
                .orderBy(qDowithTask.createdAt.asc())
                .fetchJoin()
                .fetch();
    }
}
