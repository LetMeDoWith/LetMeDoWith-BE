package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record CreateTodoTaskCommand(
    Long taskCategoryId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    Boolean isRoutine,
    TodoTaskRoutineCondition routineCondition
) {

}