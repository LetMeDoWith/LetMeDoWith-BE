package com.LetMeDoWith.LetMeDoWith.batch.dto;

import java.time.LocalTime;

public record DowithTaskWithFeedbackCountDto(
        Long id,
        String memberId,
        String nickname,
        String badgeImageUrl,
        String title,
        String status,
        LocalTime startTime,
        Long feedbackCount) {}
