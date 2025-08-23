package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateDowithTaskContentsAndCreateRoutineCommand(
        Long dowithTaskId,
        String title,
        Long taskCategoryId,
        LocalDate date,
        LocalTime startTime,
        TaskRoutineCondition taskRoutineCondition) {}
