package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import java.util.List;

public interface DowithTaskFeedbackQueryRepository {

    List<DowithTaskFeedbackQueryDto> findByTaskId(Long taskId);
}
