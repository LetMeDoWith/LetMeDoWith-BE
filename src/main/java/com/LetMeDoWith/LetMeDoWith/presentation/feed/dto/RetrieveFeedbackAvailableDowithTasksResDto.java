package com.LetMeDoWith.LetMeDoWith.presentation.feed.dto;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveFeedbackAvailableDowithTasksResult.RetrieveFeedbackAvailableDowithTaskResult;
import java.time.LocalTime;
import java.util.List;

public record RetrieveFeedbackAvailableDowithTasksResDto(List<RetrieveFeedbackAvailableDowithTaskDto> dowithTasks) {

    public static RetrieveFeedbackAvailableDowithTasksResDto from(RetrieveFeedbackAvailableDowithTasksResult result) {

        return new RetrieveFeedbackAvailableDowithTasksResDto(result.dowithTasks().stream()
                .map(RetrieveFeedbackAvailableDowithTaskDto::from)
                .toList());
    }

    public record RetrieveFeedbackAvailableDowithTaskDto(
            Long id,
            String memberId,
            String nickname,
            String badgeImageUrl,
            String title,
            String status,
            LocalTime startTime,
            Long feedbackCount) {

        public static RetrieveFeedbackAvailableDowithTaskDto from(RetrieveFeedbackAvailableDowithTaskResult result) {
            return new RetrieveFeedbackAvailableDowithTaskDto(
                    result.id(),
                    result.memberId(),
                    result.nickname(),
                    result.badgeImageUrl(),
                    result.title(),
                    result.status(),
                    result.startTime(),
                    result.feedbackCount());
        }
    }
}
