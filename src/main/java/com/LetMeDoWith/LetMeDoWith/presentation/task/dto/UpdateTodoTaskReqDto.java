package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateTodoTaskReqDto(
        @Schema(description = "제목", defaultValue = "저녁 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "Task 카테고리 ID", defaultValue = "2") Long taskCategoryId,
        @Schema(description = "시작 일자", defaultValue = "2025-01-30") @NotNull LocalDate date,
        @Schema(description = "시작 시각", defaultValue = "11:30:00") LocalTime startTime,
        UpdateTodoTaskRoutineReqDto routineCondition) {}
