package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "두윗모드 Task 잔소리 조회 DTO")
public record DowithTaskFeedbackQueryDto(
    @Schema(description = "잔소리 ID", example = "1") Long id,
    @Schema(description = "두윗모드 Task ID", example = "12345") Long dowithTaskId,
    @Schema(description = "잔소리 템플릿 ID", example = "67890") Long taskFeedbackTemplateId,
    @Schema(description = "잔소리 보낸사람 ID", example = "(TSID)") String senderId,
    @Schema(description = "잔소리 받는사람 닉네임", example = "feedbackSender123") String senderNickname,
    @Schema(description = "잔소리 받는사람 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    String senderProfileImageUrl,
    @Schema(description = "잔소리 확인여부", example = "false") Boolean isChecked) {
    
}