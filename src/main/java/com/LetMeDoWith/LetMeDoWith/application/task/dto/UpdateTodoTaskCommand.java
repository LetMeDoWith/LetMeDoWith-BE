package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import jakarta.annotation.Nullable;

import java.time.LocalTime;
import java.util.Optional;

public record UpdateTodoTaskCommand(
        String title, LocalTime startTime, Long taskCategoryId, Optional<TaskRoutineCondition> routineCondition) {

    public static UpdateTodoTaskCommand of(
            String title,
            LocalTime startTime,
            Long taskCategoryId,
            @Nullable TaskRoutineCondition routineCondition) {
        return new UpdateTodoTaskCommand(title, startTime, taskCategoryId, Optional.ofNullable(routineCondition));
    }
}
