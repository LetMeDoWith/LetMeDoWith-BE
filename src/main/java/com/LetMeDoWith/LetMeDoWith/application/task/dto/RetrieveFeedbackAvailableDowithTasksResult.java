package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto.SentFeedbacksQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.FeedbackAvailableDowithTasksQueryDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record RetrieveFeedbackAvailableDowithTasksResult(
        Long totalCount, List<FeedbackAvailableDowithTaskDto> dowithTasks) {

    public static RetrieveFeedbackAvailableDowithTasksResult from(
            Long totalCount, List<FeedbackAvailableDowithTasksQueryDto> dto) {
        List<FeedbackAvailableDowithTaskDto> dowithTasks =
                dto.stream().map(FeedbackAvailableDowithTaskDto::from).toList();
        return new RetrieveFeedbackAvailableDowithTasksResult(totalCount, dowithTasks);
    }

    public record FeedbackAvailableDowithTaskDto(
            Long id,
            String memberId,
            String nickname,
            String profileImageUrl,
            String title,
            String status,
            LocalDate date,
            LocalTime startTime,
            int feedbackCount,
            List<SentFeedbackDto> myFeedbacks) {

        public static FeedbackAvailableDowithTaskDto from(FeedbackAvailableDowithTasksQueryDto dto) {
            return new FeedbackAvailableDowithTaskDto(
                    dto.id(),
                    dto.memberId(),
                    dto.nickname(),
                    dto.profileImageUrl(),
                    dto.title(),
                    dto.status(),
                    dto.date(),
                    dto.startTime(),
                    dto.feedbackCount(),
                    dto.myFeedbacks().stream().map(SentFeedbackDto::from).toList());
        }
    }

    public record SentFeedbackDto(Long templateId, LocalDateTime createdAt) {

        public static SentFeedbackDto from(SentFeedbacksQueryDto dto) {
            return new SentFeedbackDto(dto.templateId(), dto.createdAt());
        }
    }
}
