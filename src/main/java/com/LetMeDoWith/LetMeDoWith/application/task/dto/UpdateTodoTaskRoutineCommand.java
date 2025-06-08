package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import java.time.LocalDate;
import java.util.Set;

public record UpdateTodoTaskRoutineCommand(
        LocalDate startDate,
        LocalDate endDate,
        TodoTaskRoutineCycle cycle,
        Set<Integer> pattern,
        Boolean isExcludeHolidays) {

    public static UpdateTodoTaskRoutineCommand of(
            LocalDate startDate,
            LocalDate endDate,
            TodoTaskRoutineCycle cycle,
            Set<Integer> pattern,
            Boolean isExcludeHolidays) {
        return new UpdateTodoTaskRoutineCommand(startDate, endDate, cycle, pattern, isExcludeHolidays);
    }
}
