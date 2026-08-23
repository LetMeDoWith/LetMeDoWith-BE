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
        @Schema(description = "제목", example = "저녁 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "Task 카테고리 ID", example = "2") Long taskCategoryId,
        @Schema(description = "시작 일자", example = "2025-01-30") @NotNull LocalDate date,
        @Schema(description = "시작 시각", example = "11:30:00") LocalTime startTime,
        @Schema(description = "루틴 반복 조건") UpdateTodoTaskRoutineReqDto routineCondition) {}
