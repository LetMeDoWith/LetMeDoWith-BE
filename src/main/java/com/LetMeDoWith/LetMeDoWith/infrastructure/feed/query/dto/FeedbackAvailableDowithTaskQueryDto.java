package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record FeedbackAvailableDowithTaskQueryDto(
        Long id,
        String memberId,
        String nickname,
        String badgeImageUrl,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        Long feedbackCount) {}
