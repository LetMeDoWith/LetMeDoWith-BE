package com.LetMeDoWith.LetMeDoWith.presentation.feed.dto;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetreiveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetreiveFeedbackAvailableDowithTasksResult.FeedbackAvailableDowithTaskDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RetrieveFeedbackAvailableDowithTasksResDto(List<RetrieveFeedbackAvailableDowithTaskResDto> dowithTasks) {

    public static RetrieveFeedbackAvailableDowithTasksResDto from(RetreiveFeedbackAvailableDowithTasksResult result) {
        return new RetrieveFeedbackAvailableDowithTasksResDto(result.dowithTasks().stream()
                .map(RetrieveFeedbackAvailableDowithTaskResDto::from)
                .toList());
    }

    public record RetrieveFeedbackAvailableDowithTaskResDto(
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

        public static RetrieveFeedbackAvailableDowithTaskResDto from(FeedbackAvailableDowithTaskDto dto) {
            return new RetrieveFeedbackAvailableDowithTaskResDto(
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
