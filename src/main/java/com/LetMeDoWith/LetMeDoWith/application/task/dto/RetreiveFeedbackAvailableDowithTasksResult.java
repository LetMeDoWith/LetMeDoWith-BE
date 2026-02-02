package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto.SentFeedbacksQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.FeedbackAvailableDowithTasksQueryDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record RetreiveFeedbackAvailableDowithTasksResult(List<FeedbackAvailableDowithTaskDto> dowithTasks) {

    public static RetreiveFeedbackAvailableDowithTasksResult from(List<FeedbackAvailableDowithTasksQueryDto> dto) {
        List<FeedbackAvailableDowithTaskDto> dowithTasks =
                dto.stream().map(FeedbackAvailableDowithTaskDto::from).toList();
        return new RetreiveFeedbackAvailableDowithTasksResult(dowithTasks);
    }

    public record FeedbackAvailableDowithTaskDto(
            Long id,
            String memberId,
            String nickname,
            String badgeImageUrl,
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
                    dto.badgeImageUrl(),
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
