package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalTime;

public record UpdateTodoTaskWithRoutineCommand(
        Long todoTaskId, String title, LocalTime startTime, Long taskCategoryId) {

    public static UpdateTodoTaskWithRoutineCommand of(
            Long todoTaskId, String title, LocalTime startTime, Long taskCategoryId) {
        return new UpdateTodoTaskWithRoutineCommand(todoTaskId, title, startTime, taskCategoryId);
    }
}
