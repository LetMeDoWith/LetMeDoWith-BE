package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateDowithTaskCommand(
        Long dowithTaskId,
        String title,
        Long taskCategoryId,
        LocalDate date,
        LocalTime startTime,
        TaskRoutineCondition taskRoutineCondition) {}
