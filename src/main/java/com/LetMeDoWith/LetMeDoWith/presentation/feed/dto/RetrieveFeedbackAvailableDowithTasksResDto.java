package com.LetMeDoWith.LetMeDoWith.presentation.feed.dto;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveFeedbackAvailableDowithTasksResult.RetrieveFeedbackAvailableDowithTaskResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;

public record RetrieveFeedbackAvailableDowithTasksResDto(
    @Schema(description = "잔소리 가능 두윗 태스크 목록")
    List<RetrieveFeedbackAvailableDowithTaskDto> dowithTasks) {

    public static RetrieveFeedbackAvailableDowithTasksResDto from(RetrieveFeedbackAvailableDowithTasksResult result) {

        return new RetrieveFeedbackAvailableDowithTasksResDto(result.dowithTasks().stream()
                .map(RetrieveFeedbackAvailableDowithTaskDto::from)
                .toList());
    }

    @Schema(description = "잔소리 가능 두윗 태스크 상세")
    public record RetrieveFeedbackAvailableDowithTaskDto(
            @Schema(description = "태스크 ID", example = "1")
            Long id,
            @Schema(description = "회원 ID", example = "test_member_id")
            String memberId,
            @Schema(description = "닉네임", example = "nickname")
            String nickname,
            @Schema(description = "뱃지 이미지 URL", example = "badge_url")
            String badgeImageUrl,
            @Schema(description = "태스크 제목", example = "title")
            String title,
            @Schema(description = "상태", example = "WAIT")
            String status,
            @Schema(description = "시작 시간", example = "12:00:00")
            LocalTime startTime,
            @Schema(description = "피드백 카운트", example = "0")
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
