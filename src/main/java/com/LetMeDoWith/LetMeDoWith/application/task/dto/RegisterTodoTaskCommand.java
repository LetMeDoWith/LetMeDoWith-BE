package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import jakarta.annotation.Nullable;
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

    public static RegisterTodoTaskCommand of(
            @Nullable Long taskCategoryId,
            String title,
            LocalDate date,
            @Nullable LocalTime startTime,
            @Nullable TodoTaskRoutineCondition routineCondition) {
        return RegisterTodoTaskCommand.builder()
                .taskCategoryId(taskCategoryId)
                .title(title)
                .date(date)
                .startTime(startTime)
                .routineCondition(Optional.ofNullable(routineCondition))
                .build();
    }
}
