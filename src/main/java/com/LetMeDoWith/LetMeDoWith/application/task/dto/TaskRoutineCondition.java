package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record TaskRoutineCondition(
        LocalDate startDate,
        LocalDate endDate,
        TaskRoutineCycle cycle,
        Set<Integer> pattern,
        Boolean isExcludeHolidays) {

    public static TaskRoutineCondition of(
            LocalDate startDate,
            LocalDate endDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            Boolean isExcludeHolidays) {
        return TaskRoutineCondition.builder()
                .startDate(startDate)
                .endDate(endDate)
                .cycle(cycle)
                .pattern(pattern)
                .isExcludeHolidays(isExcludeHolidays)
                .build();
    }
}
