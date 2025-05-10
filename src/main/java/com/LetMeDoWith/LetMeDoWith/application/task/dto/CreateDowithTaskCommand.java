package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;

@Builder
public record CreateDowithTaskCommand(
        String title, Long taskCategoryId, LocalDate date, LocalTime startTime) {
    public Set<LocalDate> getTargetDateSet() {
        Set<LocalDate> targetDateSet = new HashSet<>();
        targetDateSet.add(date);
        return targetDateSet;
    }
}
