package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineRepetitionCycle;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record CreateTodoTaskCommand(
    Long taskCategoryId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    Boolean isRoutine,
    TodoTaskRoutineRepetitionCycle routineRepetitionCycle,
    Set<Integer> repetitionPattern
) {

}