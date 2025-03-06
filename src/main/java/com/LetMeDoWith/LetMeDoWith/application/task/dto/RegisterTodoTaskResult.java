package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record RegisterTodoTaskResult(
    List<TodoTaskVO> todoTaskList,
    List<LocalDate> routineDates
) {
    
    public static RegisterTodoTaskResult of(List<TodoTaskVO> todoTaskList,
                                            List<LocalDate> routineDates) {
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