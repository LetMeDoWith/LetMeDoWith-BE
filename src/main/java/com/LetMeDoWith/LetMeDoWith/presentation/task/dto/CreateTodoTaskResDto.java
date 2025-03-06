package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TodoTaskVO;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record CreateTodoTaskResDto(
    List<TodoTaskVO> todoTaskList,
    List<LocalDate> routineDates
) {
    
    public static CreateTodoTaskResDto of(List<TodoTaskVO> todoTaskList,
                                          List<LocalDate> routineDates) {
        return CreateTodoTaskResDto.builder()
                                   .todoTaskList(todoTaskList)
                                   .routineDates(routineDates)
                                   .build();
    }
}