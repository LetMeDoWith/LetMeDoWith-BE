package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTodoTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.TodoTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.TodoTaskQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoTaskQueryRepositoryImpl implements TodoTaskQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QTodoTask todoTask = QTodoTask.todoTask;
    private final QTodoTaskRoutine todoTaskRoutine = QTodoTaskRoutine.todoTaskRoutine;
    private final QTaskCategory taskCategory = QTaskCategory.taskCategory;

    @Override
    public List<TodoTaskQueryDto> getTodoTasks(String memberId, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .select(Projections.constructor(
                        TodoTaskQueryDto.class,
                        todoTask.id,
                        todoTask.taskCategoryId,
                        taskCategory.title,
                        todoTask.title,
                        todoTask.status,
                        todoTask.date,
                        todoTask.startTime,
                        todoTaskRoutine))
                .from(todoTask)
                .leftJoin(taskCategory)
                .on(todoTask.taskCategoryId.eq(taskCategory.id))
                .leftJoin(todoTaskRoutine)
                .on(todoTask.routine.id.eq(todoTaskRoutine.id))
                .where(todoTask.memberId.eq(memberId).and(todoTask.date.between(startDate, endDate)))
                .fetch();
    }

    @Override
    public Optional<TodoTaskDetailQueryDto> getTodoTask(String memberId, Long todoTaskId) {

        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(
                        TodoTaskDetailQueryDto.class,
                        todoTask.id,
                        todoTask.taskCategoryId,
                        taskCategory.title,
                        todoTask.title,
                        todoTask.status,
                        todoTask.date,
                        todoTask.startTime,
                        todoTaskRoutine.id,
                        todoTaskRoutine.rangeStartDate,
                        todoTaskRoutine.rangeEndDate,
                        todoTaskRoutine.cycle,
                        todoTaskRoutine.pattern,
                        todoTaskRoutine.isExcludeHolidays))
                .from(todoTask)
                .leftJoin(taskCategory)
                .on(todoTask.taskCategoryId.eq(taskCategory.id))
                .leftJoin(todoTaskRoutine)
                .on(todoTask.routine.id.eq(todoTaskRoutine.id))
                .where(todoTask.memberId.eq(memberId).and(todoTask.id.eq(todoTaskId)))
                .fetchOne());
    }
}
