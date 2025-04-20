package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskWithRoutineCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(description = "두윗모드 Task 생성 요청")
public record CreateDowithTaskReqDto(
    @Schema(description = "제목", defaultValue = "아침 먹기")
    @NotBlank @Size(max = 40) String title,
    
    @Schema(description = "Task 카테고리 ID", defaultValue = "1")
    Long taskCategoryId,
    
    @Schema(description = "시작 일시", defaultValue = "2025-01-30T11:30:00")
    @NotNull LocalDateTime startDateTime,
    
    @Schema(description = "루틴 등록 여부", defaultValue = "true")
    @NotNull Boolean isRoutine,
    
    @Schema(description = "루틴일", defaultValue = "[\"2025-02-01\", \"2025-02-02\"]")
    List<LocalDate> routineDates
) {
    
    public CreateDowithTaskCommand toCreateDowithTaskCommand() {
        return CreateDowithTaskCommand.builder()
                                      .title(this.title)
                                      .taskCategoryId(this.taskCategoryId)
                                      .date(this.startDateTime.toLocalDate())
                                      .startTime(this.startDateTime.toLocalTime())
                                      .build();
    }
    
    public CreateDowithTaskWithRoutineCommand toCreateDowithTaskRoutineCommand() {
        return CreateDowithTaskWithRoutineCommand.builder()
                                                 .title(this.title)
                                                 .taskCategoryId(this.taskCategoryId)
                                                 .date(this.startDateTime.toLocalDate())
                                                 .startTime(this.startDateTime.toLocalTime())
                                                 .routineDates(routineDates != null ? Set.copyOf(
                                                     this.routineDates) : new HashSet<>())
                                                 .build();
    }
}
