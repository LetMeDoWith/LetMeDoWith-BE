package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QTodoTaskRepositoryImpl implements QTodoTaskRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QTodoTask qTodoTask = QTodoTask.todoTask;
    private final QTodoTask qTodoTaskRoutine = QTodoTask.todoTask;

    @Override
    public Optional<TodoTask> findTodoTaskAggregate(Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(qTodoTask)
                .leftJoin(qTodoTask.routine)
                .where(qTodoTask.id.eq(id))
                .fetchJoin()
                .fetchOne());
    }

    @Override
    public Optional<TodoTask> findTodoTaskAggregate(Long id, String memberId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(qTodoTask)
                .leftJoin(qTodoTask.routine)
                .where(qTodoTask.id.eq(id).and(qTodoTask.memberId.eq(memberId)))
                .fetchJoin()
                .fetchOne());
    }

    @Override
    public List<TodoTask> findAllTodoTaskAggregates(String memberId, LocalDate date) {
        return jpaQueryFactory
                .selectFrom(qTodoTask)
                .leftJoin(qTodoTask.routine)
                .where(qTodoTask.memberId.eq(memberId).and(qTodoTask.date.eq(date)))
                .orderBy(qTodoTask.createdAt.asc())
                .fetchJoin()
                .fetch();
    }

    @Override
    public List<TodoTask> findAllTodoTaskAggregates(String memberId, Set<LocalDate> dates) {
        return jpaQueryFactory
                .selectFrom(qTodoTask)
                .leftJoin(qTodoTask.routine)
                .where(qTodoTask.memberId.eq(memberId).and(qTodoTask.date.in(dates)))
                .orderBy(qTodoTask.createdAt.asc())
                .fetchJoin()
                .fetch();
    }

    @Override
    public List<TodoTask> findAllTodoTaskAggregates(TodoTaskRoutine todoTaskRoutine) {
        return jpaQueryFactory
                .selectFrom(qTodoTask)
                .leftJoin(qTodoTask.routine)
                .where(qTodoTask.routine.eq(todoTaskRoutine))
                .orderBy(qTodoTask.createdAt.asc())
                .fetchJoin()
                .fetch();
    }
}
