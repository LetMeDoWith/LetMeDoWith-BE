package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record FeedDowithTaskQueryDto(
        Long id,
        String memberId,
        String nickname,
        String badgeImageUrl,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        Integer feedbackCount,
        Boolean isFeedbacked) {

    public LocalDateTime startDateTime() {
        return LocalDateTime.of(date, startTime);
    }
}
