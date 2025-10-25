package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdateTodoTaskWithRoutineReqDto(
        @Schema(description = "제목", defaultValue = "저녁 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "시작 일시", defaultValue = "2025-01-30T11:30:00") LocalDateTime startDateTime,
        @Schema(description = "Task 카테고리 ID", defaultValue = "2") Long taskCategoryId) {}
