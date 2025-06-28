package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QTaskFeedbackTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.model.QTaskFeedbackTemplateMessage;
import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.TaskFeedbackTemplateQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskFeedbackTemplateQueryRepositoryImpl
    implements TaskFeedbackTemplateQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    private final QTaskFeedbackTemplate qTaskFeedbackTemplate = QTaskFeedbackTemplate.taskFeedbackTemplate;
    private final QTaskFeedbackTemplateMessage qTaskFeedbackTemplateMessage = QTaskFeedbackTemplateMessage.taskFeedbackTemplateMessage;
    
    @Override
    public List<TaskFeedbackTemplateQueryDto> getTaskFeedbackTemplates(Long id, String language) {
        return List.of();
    }
    
    @Override
    public List<TaskFeedbackTemplateQueryDto> getAllTaskFeedbackTemplates(String language) {
        return queryFactory
            .select(
                Projections.constructor(
                    TaskFeedbackTemplateQueryDto.class,
                    qTaskFeedbackTemplate.id,
                    qTaskFeedbackTemplateMessage.language,
                    qTaskFeedbackTemplateMessage.message,
                    qTaskFeedbackTemplate.emojiUrl
                )
            )
            .from(qTaskFeedbackTemplate)
            .leftJoin(qTaskFeedbackTemplateMessage)
            .where(qTaskFeedbackTemplateMessage.language.eq(language))
            .fetch();
    }
}