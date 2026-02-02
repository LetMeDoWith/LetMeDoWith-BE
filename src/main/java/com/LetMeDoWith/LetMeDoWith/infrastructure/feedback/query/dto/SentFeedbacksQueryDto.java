package com.LetMeDoWith.LetMeDoWith.infrastructure.feedback.query.dto;

import java.time.LocalDateTime;

public record SentFeedbacksQueryDto(String senderId, Long templateId, LocalDateTime createdAt) {}
