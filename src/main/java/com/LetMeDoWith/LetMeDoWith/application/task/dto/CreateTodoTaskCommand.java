package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record CreateTodoTaskCommand(
    Long taskCategoryId,
    String title,
    LocalDate date,
    LocalTime startTime,
    Boolean isRoutine,
    TodoTaskRoutineCycle routineRepetitionCycle,
    Set<Integer> routinePattern
) {

}