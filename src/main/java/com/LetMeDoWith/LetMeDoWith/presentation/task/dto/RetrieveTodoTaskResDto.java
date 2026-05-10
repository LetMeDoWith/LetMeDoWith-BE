package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveTodoTaskResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Builder;

@Schema(description = "투두 Task 단건 조회 응답")
@Builder
public record RetrieveTodoTaskResDto(
        @Schema(description = "Task ID", example = "1") Long id,
        @Schema(description = "Task 카테고리 ID", example = "1") Long taskCategoryId,
        @Schema(description = "Task 카테고리명", example = "일상") String taskCategoryName,
        @Schema(description = "제목", example = "아침 먹기") String title,
        @Schema(description = "상태", example = "WAIT") String status,
        @Schema(description = "시작 일자", example = "2025-01-30") LocalDate date,
        @Schema(description = "시작 시각", example = "11:30:00") LocalTime startTime,
        @Schema(description = "루틴 반복 조건") TodoTaskRoutineDto routineCondition) {

    public static RetrieveTodoTaskResDto from(RetrieveTodoTaskResult result) {
        return RetrieveTodoTaskResDto.builder()
                .id(result.id())
                .taskCategoryId(result.taskCategoryId())
                .taskCategoryName(result.taskCategoryName())
                .title(result.title())
                .status(result.status())
                .date(result.date())
                .startTime(result.startTime())
                .routineCondition(
                        result.routine() != null
                                ? TodoTaskRoutineDto.builder()
                                        .startDate(result.routine().startDate())
                                        .endDate(result.routine().endDate())
                                        .cycle(result.routine().cycle())
                                        .pattern(result.routine().pattern())
                                        .isExcludeHolidays(result.routine().isExcludeHolidays())
                                        .build()
                                : null)
                .build();
    }

    @Builder
    public record TodoTaskRoutineDto(
            @Schema(description = "시작 일자", defaultValue = "2025-01-30") @NotNull LocalDate startDate,
            @Schema(description = "종료 일자", defaultValue = "2025-01-30") @NotNull LocalDate endDate,
            @Schema(description = "루틴 반복 주기", defaultValue = "DAILY") @NotNull String cycle,
            @Schema(description = "루틴 반복 패턴", defaultValue = "[1, 2, 3]") @Null Set<Integer> pattern,
            @Schema(description = "루틴 반복 휴일 제외 여부", defaultValue = "false") @NotNull Boolean isExcludeHolidays) {}
}
