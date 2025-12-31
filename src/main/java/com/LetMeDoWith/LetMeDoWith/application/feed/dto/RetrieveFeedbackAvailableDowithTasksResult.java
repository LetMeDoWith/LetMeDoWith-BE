package com.LetMeDoWith.LetMeDoWith.application.feed.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto.FeedbackAvailableDowithTaskQueryDto;
import java.time.LocalTime;
import java.util.List;

public record RetrieveFeedbackAvailableDowithTasksResult(List<RetrieveFeedbackAvailableDowithTaskResult> dowithTasks) {

    public static RetrieveFeedbackAvailableDowithTasksResult from(List<FeedbackAvailableDowithTaskQueryDto> queryDtos) {
        return new RetrieveFeedbackAvailableDowithTasksResult(queryDtos.stream()
                .map(RetrieveFeedbackAvailableDowithTaskResult::from)
                .toList());
    }

    public record RetrieveFeedbackAvailableDowithTaskResult(
            Long id,
            String memberId,
            String nickname,
            String badgeImageUrl,
            String title,
            String status,
            LocalTime startTime,
            Long feedbackCount) {

        public static RetrieveFeedbackAvailableDowithTaskResult from(FeedbackAvailableDowithTaskQueryDto queryDto) {
            return new RetrieveFeedbackAvailableDowithTaskResult(
                    queryDto.id(),
                    queryDto.memberId(),
                    queryDto.nickname(),
                    queryDto.badgeImageUrl(),
                    queryDto.title(),
                    queryDto.status(),
                    queryDto.startTime(),
                    queryDto.feedbackCount());
        }
    }
}
