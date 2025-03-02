package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TodoTaskVO;
import java.util.List;

public record CreateTodoTaskResDto(
    List<TodoTaskVO> todoTaskList,
    boolean isRoutine
) {
    
    public static CreateTodoTaskResDto of(List<TodoTaskVO> todoTaskList,
                                          boolean isRoutine) {
        return new CreateTodoTaskResDto(todoTaskList, isRoutine);
    }
}