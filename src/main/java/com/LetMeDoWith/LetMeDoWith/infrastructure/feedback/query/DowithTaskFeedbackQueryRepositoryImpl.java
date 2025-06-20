package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DowithTaskFeedbackQueryRepositoryImpl implements DowithTaskFeedbackQueryRepository {
    
    private final JPAQueryFactory queryFactory;
    
    @Override
    public List<DowithTaskFeedbackQueryDto> findByTaskId(Long taskId) {
        // TODO: Implement the logic to retrieve feedback for a specific task ID
        return List.of();
    }
}