package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record CreateDowithTaskCommand(
        String title,
        Long taskCategoryId,
        LocalDate date,
        LocalTime startTime) {
}
