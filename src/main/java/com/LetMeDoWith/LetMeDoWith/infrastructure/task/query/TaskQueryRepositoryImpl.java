package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.task.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.dto.TodoTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTaskConfirm;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TaskQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskQueryRepositoryImpl implements TaskQueryRepository {
    
    // TODO - 추후 PK 정책에 따른 수정 필요
    private final JPAQueryFactory queryFactory;
    
    private final QTodoTask todoTask = QTodoTask.todoTask;
    private final QDowithTask dowithTask = QDowithTask.dowithTask;
    
    private final QDowithTaskConfirm dowithTaskConfirm = QDowithTaskConfirm.dowithTaskConfirm;
    
    private final QTaskCategory taskCategory = QTaskCategory.taskCategory;
    private final QMember member = QMember.member;
    
    
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
    
    @Override
    public List<DowithTaskQueryDto> getDowithTasks(Long memberId, LocalDate startDate,
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
                dowithTaskConfirm.imageUrl,
                Expressions.constant(0) // TODO - 추후 FeedBack 개발시 추가
            ))
            .from(dowithTask)
            .leftJoin(taskCategory).on(dowithTask.taskCategoryId.eq(taskCategory.id))
            .leftJoin(dowithTaskConfirm).on(dowithTaskConfirm.dowithTask.eq(dowithTask))
            .where(dowithTask.memberId.eq(memberId)
                                      .and(dowithTask.date.between(startDate, endDate)))
            .fetch();
    }
    
}
