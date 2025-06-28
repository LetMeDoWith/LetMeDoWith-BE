package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto;

public record TaskFeedbackTemplateQueryDto(
    Long id,
    String language,
    String message,
    String emojiUrl
) {

}