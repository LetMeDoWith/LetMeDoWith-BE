package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTaskConfirm;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTaskCategory;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DowithTaskQueryRespositoryImpl implements DowithTaskQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QDowithTask dowithTask = QDowithTask.dowithTask;
    private final QDowithTaskConfirm dowithTaskConfirm = QDowithTaskConfirm.dowithTaskConfirm;
    private final QTaskCategory taskCategory = QTaskCategory.taskCategory;

    @Override
    public List<DowithTaskQueryDto> getDowithTasks(
            String memberId, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .select(
                        Projections.constructor(
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
                .leftJoin(taskCategory)
                .on(dowithTask.taskCategoryId.eq(taskCategory.id))
                .leftJoin(dowithTaskConfirm)
                .on(dowithTaskConfirm.dowithTask.eq(dowithTask))
                .where(dowithTask.memberId.eq(memberId).and(dowithTask.date.between(startDate, endDate)))
                .fetch();
    }
}
