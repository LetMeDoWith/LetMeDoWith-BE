package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record TodoTaskRoutineCondition(
        LocalDate startDate,
        LocalDate endDate,
        TaskRoutineCycle cycle,
        Set<Integer> pattern,
        Boolean isExcludeHolidays) {

    public static TodoTaskRoutineCondition of(
            LocalDate startDate,
            LocalDate endDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            Boolean isExcludeHolidays) {
        return TodoTaskRoutineCondition.builder()
                .startDate(startDate)
                .endDate(endDate)
                .cycle(cycle)
                .pattern(pattern)
                .isExcludeHolidays(isExcludeHolidays)
                .build();
    }
}
