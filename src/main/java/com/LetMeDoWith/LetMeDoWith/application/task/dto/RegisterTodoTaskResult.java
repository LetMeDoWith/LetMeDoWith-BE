package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record RegisterTodoTaskResult(
    List<TodoTask> todoTaskList,
    Set<LocalDate> routineDates
) {
    
    public static RegisterTodoTaskResult of(List<TodoTask> todoTaskList,
                                            Set<LocalDate> routineDates) {
        return RegisterTodoTaskResult.builder()
                                     .todoTaskList(todoTaskList)
                                     .routineDates(routineDates)
                                     .build();
    }
    
    public static RegisterTodoTaskResult of(TodoTask todoTask) {
        return RegisterTodoTaskResult.builder()
                                     .todoTaskList(List.of(todoTask))
                                     .routineDates(null)
                                     .build();
    }
}