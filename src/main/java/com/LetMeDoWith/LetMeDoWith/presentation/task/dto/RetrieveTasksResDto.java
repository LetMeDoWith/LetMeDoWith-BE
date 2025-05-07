package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.dto.TodoTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RetrieveTasksResDto(
    List<TodoTaskDto> todoTasks,
    List<DowithTaskDto> dowithTasks
) {
    
    public static RetrieveTasksResDto of(
        List<TodoTaskQueryDto> todoTaskQueryDtos,
        List<DowithTaskQueryDto> dowithTaskQueryDtos
    ) {
        List<TodoTaskDto> todoTasks = todoTaskQueryDtos.stream()
                                                       .map(todoTaskQueryDto -> new TodoTaskDto(
                                                           todoTaskQueryDto.id(),
                                                           todoTaskQueryDto.taskCategoryId(),
                                                           todoTaskQueryDto.taskCategoryName(),
                                                           todoTaskQueryDto.title(),
                                                           todoTaskQueryDto.status(),
                                                           todoTaskQueryDto.date(),
                                                           todoTaskQueryDto.startTime()
                                                       )).toList();
        
        List<DowithTaskDto> dowithTasks = dowithTaskQueryDtos.stream()
                                                             .map(dowithTaskQueryDto -> new DowithTaskDto(
                                                                 dowithTaskQueryDto.id(),
                                                                 dowithTaskQueryDto.taskCategoryId(),
                                                                 dowithTaskQueryDto.taskCategoryName(),
                                                                 dowithTaskQueryDto.title(),
                                                                 dowithTaskQueryDto.status(),
                                                                 dowithTaskQueryDto.date(),
                                                                 dowithTaskQueryDto.startTime(),
                                                                 dowithTaskQueryDto.confirmedImageUrl(),
                                                                 dowithTaskQueryDto.feedBackCount()
                                                             )).toList();
        
        return RetrieveTasksResDto.builder()
                                  .todoTasks(todoTasks)
                                  .dowithTasks(dowithTasks)
                                  .build();
    }
    
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
        String taskCategoryName,
        String title,
        DowithTaskStatus status,
        LocalDate date,
        LocalTime startTime,
        String confirmedImageUrl,
        int feedBackCount
    ) {
    
    }
}
