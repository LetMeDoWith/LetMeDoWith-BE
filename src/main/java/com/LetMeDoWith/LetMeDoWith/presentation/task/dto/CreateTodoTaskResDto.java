package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Builder
public record CreateTodoTaskResDto(
    List<TodoTaskVO> todoTaskList,
    Set<LocalDate> routineDates
) {
    
    public static CreateTodoTaskResDto of(List<TodoTaskVO> todoTaskList,
                                          Set<LocalDate> routineDates) {
        return CreateTodoTaskResDto.builder()
                                   .todoTaskList(todoTaskList)
                                   .routineDates(routineDates)
                                   .build();
    }
    
    @Builder
    @Getter
    public static class TodoTaskVO {
        
        private final Long id;
        private final Long taskCategoryId;
        private final String title;
        private final LocalDate date;
        private final LocalTime startTime;
        private final Boolean isRoutine;
        
        public static TodoTaskVO from(TodoTask todoTask) {
            return TodoTaskVO.builder()
                             .id(todoTask.getId())
                             .taskCategoryId(todoTask.getTaskCategoryId())
                             .title(todoTask.getTitle())
                             .date(todoTask.getDate())
                             .startTime(todoTask.getStartTime())
                             .isRoutine(todoTask.getRoutine() != null)
                             .build();
        }
    }
}