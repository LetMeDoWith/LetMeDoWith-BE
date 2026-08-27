package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record CreateDowithTaskWithRoutineCommand(
        Long taskCategoryId,
        String title,
        LocalDate date,
        LocalTime startTime,
        TaskRoutineCondition routineCondition) {}
