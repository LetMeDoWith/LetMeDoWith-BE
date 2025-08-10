package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record CreateDowithTaskWithRoutineCommand(
        Long taskCategoryId,
        String title,
        LocalDate date,
        LocalTime startTime,
        TaskRoutineCondition routineCondition) {
}
