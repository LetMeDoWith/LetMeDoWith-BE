package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto;

public record DowithTaskFeedbackQueryDto(
    Long id,
    Long dowithTaskId,
    Long taskFeedbackTemplateId,
    String senderId,
    String senderName,
    String senderProfileImageUrl,
    Boolean isChecked
) {

}