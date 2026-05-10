package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.SortDirection;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.CountSentFeedback;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.DowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.SentDowithTaskFeedbackQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.SentFeedbacksQueryDto;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DowithTaskFeedbackQueryRepository {

    Long countFeedbacksByTaskId(Long taskId, @Nullable Long templateId);

    List<DowithTaskFeedbackQueryDto> getFeedbacksByTaskId(
            Long taskId, @Nullable Long templateId, Long offset, int size);

    Long countFeedbacksBySenderId(String senderId);

    List<SentDowithTaskFeedbackQueryDto> getFeedbacksBySenderId(String senderId, Long offset, int limit);

    Long countFeedbacksByReceiverId(String receiverId);

    List<DowithTaskFeedbackQueryDto> getFeedbacksByReceiverId(String receiverId, Long offset, int limit);

    Map<Long, Long> countFeedbacksByTaskIds(List<Long> taskIds);

    Map<Long, List<SentFeedbacksQueryDto>> getSentFeedbacks(String senderId, List<Long> taskIds);

    List<CountSentFeedback> getCountSentFeedbacks(
            LocalDate rangeStartDate, LocalDate rangeEndDate, SortDirection sortDirection);
}
