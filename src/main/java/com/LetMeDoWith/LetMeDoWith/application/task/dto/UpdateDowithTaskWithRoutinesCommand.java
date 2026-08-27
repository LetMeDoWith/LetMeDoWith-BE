package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateDowithTaskWithRoutinesCommand(
        Long id,
        String title,
        Long taskCategoryId,
        LocalDate date,
        LocalTime startTime,
        Boolean isRoutine,
        Set<LocalDate> routineDates) {}
