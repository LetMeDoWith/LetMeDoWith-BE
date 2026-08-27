package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record CreateTodoTaskResult(List<TodoTask> todoTaskList, Set<LocalDate> routineDates) {

    public static CreateTodoTaskResult of(List<TodoTask> todoTaskList, Set<LocalDate> routineDates) {
        return CreateTodoTaskResult.builder()
                .todoTaskList(todoTaskList)
                .routineDates(routineDates)
                .build();
    }

    public static CreateTodoTaskResult of(TodoTask todoTask) {
        return CreateTodoTaskResult.builder()
                .todoTaskList(List.of(todoTask))
                .routineDates(null)
                .build();
    }
}
