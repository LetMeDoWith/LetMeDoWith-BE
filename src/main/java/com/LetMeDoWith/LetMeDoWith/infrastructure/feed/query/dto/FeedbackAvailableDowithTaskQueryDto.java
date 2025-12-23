package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto;

import java.time.LocalTime;

public record FeedbackAvailableDowithTaskQueryDto(
        Long id,
        String memberId,
        String nickname,
        String badgeImageUrl,
        String title,
        String status,
        LocalTime startTime,
        Long feedbackCount) {}
