package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Schema(description = "투두모드 Task 생성 요청")
public record CreateTodoTaskReqDto(
        @Schema(description = "제목", example = "아침 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "Task 카테고리 ID", example = "1") Long taskCategoryId,
        @Schema(description = "시작 일자", example = "2025-01-30") @NotNull LocalDate date,
        @Schema(description = "시작 시각", type = "string", pattern = "HH:mm", example = "11:30:00") LocalTime startTime,
        @Schema(description = "루틴 반복 정보") TodoTaskRoutineCondition routineCondition) {

    public record TodoTaskRoutineCondition(
            @Schema(description = "시작 일자", example = "2025-01-30") @NotNull LocalDate startDate,
            @Schema(description = "종료 일자", example = "2025-02-28") @NotNull LocalDate endDate,
            @Schema(description = "루틴 반복 주기", example = "DAILY") @NotNull TaskRoutineCycle cycle,
            @Schema(description = "루틴 반복 패턴", example = "[1, 2, 3]") @NotNull Set<Integer> pattern,
            @Schema(description = "루틴 반복 휴일 제외 여부", example = "false") @NotNull Boolean isExcludeHolidays) {}
}
