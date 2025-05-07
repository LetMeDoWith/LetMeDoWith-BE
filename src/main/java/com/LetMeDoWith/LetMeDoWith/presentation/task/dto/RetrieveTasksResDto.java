package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record GetTasksResDto(
    List<TodoTaskDto> todoTasks,
    List<DowithTaskDto> dowithTasks
) {
    
    public record TodoTaskDto(
        Long id, // TODO - 추후 PK 정책에 따른 수정 필요
        Long taskCategoryId,
        String taskCategoryName,
        String title,
        TodoTaskStatus status,
        LocalDate date,
        LocalTime startTime
    ) {
    
    }
    
    public record DowithTaskDto(
        Long id, // TODO - 추후 PK 정책에 따른 수정 필요
        Long taskCategoryId,
        Long taskCategoryName,
        String title,
        DowithTaskStatus status,
        LocalDate date,
        LocalTime startTime,
        String confirmedImageUrl,
        int feedBackCount
    ) {
    
    }
}
