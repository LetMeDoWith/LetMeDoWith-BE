package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskContentsCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateDowithTaskReqDto(
    @Schema(description = "두윗모드 Task ID", defaultValue = "1")
    @NotNull Long dowithTaskId,
    
    @Schema(description = "제목", defaultValue = "저녁 먹기")
    @NotBlank String title,
    
    @Schema(description = "Task 카테고리 ID", defaultValue = "2")
    Long taskCategoryId,
    
    @Schema(description = "시작일시", defaultValue = "2025-03-30T11:30:00")
    LocalDateTime startDateTime,
    
    @Schema(description = "루틴 생성 여부", defaultValue = "true")
    @NotNull Boolean isRoutineCreate,
    
    @Schema(description = "루틴일", defaultValue = "[\"2025-04-01\", \"2025-04-02\"]")
    List<LocalDate> routineDates,
    
    @Schema(description = "투두모드로 전환 여부", defaultValue = "false")
    @NotNull Boolean isConvertToTodoTask
) {
    
    public UpdateDowithTaskContentsCommand toCommand() {
        return UpdateDowithTaskContentsCommand.builder()
                                              .id(dowithTaskId)
                                              .title(this.title)
                                              .taskCategoryId(this.taskCategoryId)
                                              .date(this.startDateTime().toLocalDate())
                                              .startTime(this.startDateTime().toLocalTime())
                                              .isConvertToTodoTask(this.isConvertToTodoTask)
                                              .build();
    }
    
    public Set<LocalDate> getRoutineDates() {
        Set<LocalDate> routineDates = new HashSet<>(this.routineDates);
        routineDates.add(startDateTime.toLocalDate());
        return routineDates;
    }
    
}
