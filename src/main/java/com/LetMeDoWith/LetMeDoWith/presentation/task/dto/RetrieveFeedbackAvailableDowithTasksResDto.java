package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveFeedbackAvailableDowithTasksResult.FeedbackAvailableDowithTaskDto;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveFeedbackAvailableDowithTasksResult.SentFeedbackDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "피드백 가능한 두윗 태스크 목록 조회 응답 DTO")
public record RetrieveFeedbackAvailableDowithTasksResDto(
        @Schema(description = "두윗 태스크 목록") List<RetrieveFeedbackAvailableDowithTaskResDto> dowithTasks) {

    public static RetrieveFeedbackAvailableDowithTasksResDto from(RetrieveFeedbackAvailableDowithTasksResult result) {
        return new RetrieveFeedbackAvailableDowithTasksResDto(result.dowithTasks().stream()
                .map(RetrieveFeedbackAvailableDowithTaskResDto::from)
                .toList());
    }

    @Schema(description = "피드백 가능한 두윗 태스크 상세 정보")
    public record RetrieveFeedbackAvailableDowithTaskResDto(
            @Schema(description = "두윗 태스크 ID", example = "1") Long id,
            @Schema(description = "작성자 회원 ID", example = "test_member_id") String memberId,
            @Schema(description = "작성자 닉네임", example = "두윗러") String nickname,
            @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png") String profileImageUrl,
            @Schema(description = "두윗 제목", example = "오늘의 운동 인증") String title,
            @Schema(description = "두윗 상태 (WAIT: 대기중, IN_PROGRESS: 진행중, COMPLETE: 완료)", example = "WAIT") String status,
            @Schema(description = "두윗 수행 날짜", example = "2023-10-01") LocalDate date,
            @Schema(description = "두윗 시작 시간", example = "14:00:00") LocalTime startTime,
            @Schema(description = "전체 피드백 개수", example = "5") int feedbackCount,
            @Schema(description = "내가 보낸 피드백 목록") List<SentFeedbackResDto> myFeedbacks) {

        public static RetrieveFeedbackAvailableDowithTaskResDto from(FeedbackAvailableDowithTaskDto dto) {
            return new RetrieveFeedbackAvailableDowithTaskResDto(
                    dto.id(),
                    dto.memberId(),
                    dto.nickname(),
                    dto.profileImageUrl(),
                    dto.title(),
                    dto.status(),
                    dto.date(),
                    dto.startTime(),
                    dto.feedbackCount(),
                    dto.myFeedbacks().stream().map(SentFeedbackResDto::from).toList());
        }
    }

    @Schema(description = "내가 보낸 피드백 정보")
    public record SentFeedbackResDto(
            @Schema(description = "피드백 템플릿 ID", example = "1") Long templateId,
            @Schema(description = "피드백 전송 일시", example = "2023-10-01T14:30:00") LocalDateTime createdAt) {

        public static SentFeedbackResDto from(SentFeedbackDto dto) {
            return new SentFeedbackResDto(dto.templateId(), dto.createdAt());
        }
    }
}
