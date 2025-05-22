package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import java.time.LocalDate;
import java.util.Set;

public record UpdateTodoTaskRoutineConditionReqDto(
    LocalDate startDate,
    LocalDate endDate,
    TodoTaskRoutineCycle cycle,
    Set<Integer> pattern,
    Boolean isExcludeHolidays
) {

}