package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;

@Builder
public record CreateDowithTaskWithRoutineCommand(
    String title,
    Long taskCategoryId,
    LocalDate date,
    LocalTime startTime,
    Set<LocalDate> routineDates
) {
    
    public Set<LocalDate> getTargetDateSet() {
        Set<LocalDate> targetDateSet = new HashSet<>();
        targetDateSet.add(date);
        targetDateSet.addAll(routineDates);
        return targetDateSet;
    }
}
