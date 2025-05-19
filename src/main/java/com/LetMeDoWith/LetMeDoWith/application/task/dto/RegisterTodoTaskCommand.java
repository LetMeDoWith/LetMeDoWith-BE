package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import lombok.Builder;

@Builder
public record RegisterTodoTaskCommand(
    Long taskCategoryId,
    String title,
    LocalDate date,
    LocalTime startTime,
    
    Optional<TodoTaskRoutineCondition> routineCondition) {
    
}