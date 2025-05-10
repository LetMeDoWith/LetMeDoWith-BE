package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTodoTask;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoTaskQueryRepositoryImpl implements TodoTaskQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    private final QTodoTask todoTask = QTodoTask.todoTask;
    private final QTaskCategory taskCategory = QTaskCategory.taskCategory;
    
    
    @Override
    public List<TodoTaskQueryDto> getTodoTasks(Long memberId, LocalDate startDate,
                                               LocalDate endDate) {
        return queryFactory
            .select(Projections
                        .constructor(TodoTaskQueryDto.class,
                                     todoTask.id,
                                     todoTask.taskCategoryId,
                                     taskCategory.title,
                                     todoTask.title,
                                     todoTask.status,
                                     todoTask.date,
                                     todoTask.startTime
                        ))
            .from(todoTask)
            .leftJoin(taskCategory).on(todoTask.taskCategoryId.eq(taskCategory.id))
            .where(todoTask.memberId.eq(memberId)
                                    .and(todoTask.date.between(startDate, endDate)))
            .fetch();
    }
    
    
}
