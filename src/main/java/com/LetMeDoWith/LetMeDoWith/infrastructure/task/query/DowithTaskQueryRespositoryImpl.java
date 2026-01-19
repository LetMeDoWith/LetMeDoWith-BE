package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTaskSuccess;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QTaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.DowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
}
