package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record RegisterTodoTaskResult(
    List<TodoTaskVO> todoTaskList,
    boolean isRoutine
) {
    
    public static RegisterTodoTaskResult of(List<TodoTaskVO> todoTaskList,
                                            boolean isRoutine) {
        return RegisterTodoTaskResult.builder()
                                     .todoTaskList(todoTaskList)
                                     .isRoutine(isRoutine)
                                     .build();
    }
    
    public static RegisterTodoTaskResult of(TodoTaskVO todoTaskVO) {
        return RegisterTodoTaskResult.builder()
                                     .todoTaskList(List.of(todoTaskVO))
                                     .isRoutine(false)
                                     .build();
    }
    
}