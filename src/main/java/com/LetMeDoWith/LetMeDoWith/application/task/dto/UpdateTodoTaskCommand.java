package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import jakarta.annotation.Nullable;
import java.time.LocalTime;
import java.util.Optional;

public record UpdateTodoTaskCommand(
        String title,
        LocalTime startTime,
        Long taskCategoryId,
        Optional<TodoTaskRoutineCondition> routineCondition) {

    public static UpdateTodoTaskCommand of(
            String title,
            LocalTime startTime,
            Long taskCategoryId,
            @Nullable TodoTaskRoutineCondition routineCondition) {
        return new UpdateTodoTaskCommand(
                title, startTime, taskCategoryId, Optional.ofNullable(routineCondition));
    }
}
