package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.DowithTaskFeedbackQueryDto;
import java.util.List;

public interface DowithTaskFeedbackQueryRepository {
    
    List<DowithTaskFeedbackQueryDto> findAllByTaskId(Long taskId);
    
    List<DowithTaskFeedbackQueryDto> findAllBySenderId(String senderId);
    
    List<DowithTaskFeedbackQueryDto> findAllByReceiverId(String receiverId);
}