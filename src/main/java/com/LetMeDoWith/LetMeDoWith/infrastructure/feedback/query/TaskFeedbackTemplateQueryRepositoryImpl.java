package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QTaskFeedbackTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QTaskFeedbackTemplateMessage;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.TaskFeedbackTemplateQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.TaskFeedbackTemplateQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TaskFeedbackTemplateQueryRepositoryImpl implements TaskFeedbackTemplateQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QTaskFeedbackTemplate qTaskFeedbackTemplate = QTaskFeedbackTemplate.taskFeedbackTemplate;
    private final QTaskFeedbackTemplateMessage qTaskFeedbackTemplateMessage =
            QTaskFeedbackTemplateMessage.taskFeedbackTemplateMessage;

    // TODO: 향후 캐싱 적용
    @Override
    public List<TaskFeedbackTemplateQueryDto> getAllTaskFeedbackTemplates(CountryCode language) {
        return queryFactory
                .select(Projections.constructor(
                        TaskFeedbackTemplateQueryDto.class,
                        qTaskFeedbackTemplate.id,
                        qTaskFeedbackTemplateMessage.language,
                        qTaskFeedbackTemplateMessage.message,
                        qTaskFeedbackTemplate.emojiUrl))
                .from(qTaskFeedbackTemplate)
                .leftJoin(qTaskFeedbackTemplateMessage)
                .on(qTaskFeedbackTemplate.id.eq(qTaskFeedbackTemplateMessage.taskFeedbackTemplate.id))
                .where(qTaskFeedbackTemplateMessage.language.eq(language))
                .fetch();
    }
}
