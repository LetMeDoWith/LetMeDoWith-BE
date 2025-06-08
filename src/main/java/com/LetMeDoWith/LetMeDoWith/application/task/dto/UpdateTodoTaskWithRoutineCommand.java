package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalTime;

public record UpdateTodoTaskWithRoutineCommand(
        String title, LocalTime startTime, Long taskCategoryId) {

    public static UpdateTodoTaskWithRoutineCommand of(
            String title, LocalTime startTime, Long taskCategoryId) {
        return new UpdateTodoTaskWithRoutineCommand(title, startTime, taskCategoryId);
    }
}
