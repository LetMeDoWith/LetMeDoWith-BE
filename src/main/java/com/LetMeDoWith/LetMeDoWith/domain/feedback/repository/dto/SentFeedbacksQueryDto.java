package com.LetMeDoWith.LetMeDoWith.domain.feedback.repository.dto;

import java.time.LocalDateTime;

public record SentFeedbacksQueryDto(String senderId, Long templateId, LocalDateTime createdAt) {
}
