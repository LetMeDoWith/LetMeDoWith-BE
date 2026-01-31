package com.LetMeDoWith.LetMeDoWith.application.feed.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedDowithTaskQueryDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RetreiveFeedbackAvailableDowithTasksResult(List<FeedbackAvailableDowithTaskDto> dowithTasks) {

    public static RetreiveFeedbackAvailableDowithTasksResult from(List<FeedDowithTaskQueryDto> dto) {
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
            Integer feedbackCount,
            Boolean isFeedbacked) {
        public static FeedbackAvailableDowithTaskDto from(FeedDowithTaskQueryDto dto) {
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
                    dto.isFeedbacked());
        }
    }
}
