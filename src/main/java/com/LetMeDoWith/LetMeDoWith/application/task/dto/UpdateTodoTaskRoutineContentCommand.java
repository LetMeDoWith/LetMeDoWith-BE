package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalTime;

public record UpdateTodoTaskRoutineContentCommand(
    String title,
    LocalTime startTime,
    Long taskCategoryId,
    Boolean isApplyToAll
) {
    
    public static UpdateTodoTaskRoutineContentCommand of(
        String title,
        LocalTime startTime,
        Long taskCategoryId,
        Boolean isApplyToAll
    ) {
        return new UpdateTodoTaskRoutineContentCommand(title,
                                                       startTime,
                                                       taskCategoryId,
                                                       isApplyToAll);
    }
    
}