package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.SortDirection;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DowithTaskFeedbackQueryRepository {

    Long countFeedbacksByTaskId(Long taskId);

    List<DowithTaskFeedbackQueryDto> getFeedbacksByTaskId(Long taskId, Long offset, int size);

    Long countFeedbacksBySenderId(String senderId);

    List<SentDowithTaskFeedbackQueryDto> getFeedbacksBySenderId(String senderId, Long offset, int limit);

    Long countFeedbacksByReceiverId(String receiverId);

    List<DowithTaskFeedbackQueryDto> getFeedbacksByReceiverId(String receiverId, Long offset, int limit);

    Map<Long, Long> countFeedbacksByTaskIds(List<Long> taskIds);

    Map<Long, List<SentFeedbacksQueryDto>> getSentFeedbacks(String senderId, List<Long> taskIds);

    List<CountSentFeedback> getCountSentFeedbacks(
            LocalDate rangeStartDate, LocalDate rangeEndDate, SortDirection sortDirection);

    List<AggregateTaskFeedbacksQueryDto> aggregateTaskFeedbacks(Long taskId);
}
