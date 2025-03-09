package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record RegisterTodoTaskResult(
    List<TodoTaskVO> todoTaskList,
    Set<LocalDate> routineDates
) {
    
    public static RegisterTodoTaskResult of(List<TodoTaskVO> todoTaskList,
                                            Set<LocalDate> routineDates) {
        return RegisterTodoTaskResult.builder()
                                     .todoTaskList(todoTaskList)
                                     .routineDates(routineDates)
                                     .build();
    }
    
    public static RegisterTodoTaskResult of(TodoTaskVO todoTaskVO) {
        return RegisterTodoTaskResult.builder()
                                     .todoTaskList(List.of(todoTaskVO))
                                     .routineDates(null)
                                     .build();
    }
    
}