package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.DowithTaskFeedbackQueryDto;
import java.util.List;

public interface DowithTaskFeedbackQueryRepository {

    Long countFeedbacksByTaskId(Long taskId);

    List<DowithTaskFeedbackQueryDto> getFeedbacksByTaskId(Long taskId, Long offset, int size);

    Long countFeedbacksBySenderId(String senderId);

    List<DowithTaskFeedbackQueryDto> getFeedbacksBySenderId(String senderId, Long offset, int limit);

    Long countFeedbacksByReceiverId(String receiverId);

    List<DowithTaskFeedbackQueryDto> getFeedbacksByReceiverId(String receiverId, Long offset, int limit);
}
