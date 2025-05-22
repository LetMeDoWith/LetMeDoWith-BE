package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import java.time.LocalDate;
import java.util.Set;

public record UpdateTodoTaskRoutineConditionCommand(
    LocalDate startDate,
    LocalDate endDate,
    TodoTaskRoutineCycle cycle,
    Set<Integer> pattern,
    Boolean isExcludeHolidays) {
    
    public static UpdateTodoTaskRoutineConditionCommand of(
        LocalDate startDate,
        LocalDate endDate,
        TodoTaskRoutineCycle cycle,
        Set<Integer> pattern,
        Boolean isExcludeHolidays) {
        return new UpdateTodoTaskRoutineConditionCommand(startDate,
                                                         endDate,
                                                         cycle,
                                                         pattern,
                                                         isExcludeHolidays);
    }
    
}