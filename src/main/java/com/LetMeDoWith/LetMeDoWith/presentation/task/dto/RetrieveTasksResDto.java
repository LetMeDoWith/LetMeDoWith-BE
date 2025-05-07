package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.dto.TodoTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(description = "투두모드Task ID", defaultValue = "1")
        Long id,
        @Schema(description = "Task카테고리 ID", defaultValue = "2")
        Long taskCategoryId,
        @Schema(description = "카테고리명", defaultValue = "일상")
        String taskCategoryName,
        @Schema(description = "제목", defaultValue = "아침 먹기")
        String title,
        @Schema(description = "상태", implementation = TodoTaskStatus.class)
        TodoTaskStatus status,
        @Schema(description = "일자", defaultValue = "2025-01-30")
        LocalDate date,
        @Schema(description = "시작시간", defaultValue = "11:30:00")
        LocalTime startTime
    ) {
    
    }
    
    public record DowithTaskDto(
        @Schema(description = "두윗모드Task ID", defaultValue = "1")
        Long id,
        @Schema(description = "Task카테고리 ID", defaultValue = "2")
        Long taskCategoryId,
        @Schema(description = "카테고리명", defaultValue = "일상")
        String taskCategoryName,
        @Schema(description = "제목", defaultValue = "아침 먹기")
        String title,
        @Schema(description = "상태", implementation = DowithTaskStatus.class)
        DowithTaskStatus status,
        @Schema(description = "일자", defaultValue = "2025-01-30")
        LocalDate date,
        @Schema(description = "시작시간", defaultValue = "11:30:00")
        LocalTime startTime,
        @Schema(description = "인증 이미지 URL", defaultValue = "https://example.com/image.jpg")
        String confirmedImageUrl,
        @Schema(description = "피드백 개수", defaultValue = "5")
        int feedBackCount
    ) {
    
    }
}
