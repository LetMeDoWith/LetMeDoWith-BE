package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveDowithTaskResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record RetrieveDowithTaskResDto(
        @Schema(description = "Task ID", example = "1") Long id,
        @Schema(description = "Task 카테고리 ID", example = "1") Long taskCategoryId,
        @Schema(description = "Task 카테고리명", example = "일상") String taskCategoryName,
        @Schema(description = "제목", example = "아침 먹기") String title,
        @Schema(description = "상태", example = "WAIT") String status,
        @Schema(description = "시작 일자", example = "2025-01-30") LocalDate date,
        @Schema(description = "시작 시각", example = "11:30:00") LocalTime startTime,
        @Schema(description = "인증 사진", example = "[\"https://example.com/success1.jpg\"]")
                List<String> successImageUrls,
        @Schema(description = "루틴 반복 조건") RetrieveDowithTaskResDto.DowithTaskRoutine routineCondition,
        @Schema(description = "잔소리 개수", example = "102") Long feedBackCount) {
    public static RetrieveDowithTaskResDto from(RetrieveDowithTaskResult result) {
        return RetrieveDowithTaskResDto.builder()
                .id(result.id())
                .taskCategoryId(result.taskCategoryId())
                .taskCategoryName(result.taskCategoryName())
                .title(result.title())
                .status(result.status())
                .date(result.date())
                .startTime(result.startTime())
                .successImageUrls(result.successImageUrls())
                .routineCondition(
                        result.routine() != null
                                ? RetrieveDowithTaskResDto.DowithTaskRoutine.builder()
                                        .startDate(result.routine().startDate())
                                        .endDate(result.routine().endDate())
                                        .cycle(result.routine().cycle())
                                        .pattern(result.routine().pattern())
                                        .isExcludeHolidays(result.routine().isExcludeHolidays())
                                        .build()
                                : null)
                .feedBackCount(result.feedBackCount())
                .build();
    }

    @Builder
    public record DowithTaskRoutine(
            @Schema(description = "시작 일자", example = "2025-01-30") @NotNull LocalDate startDate,
            @Schema(description = "종료 일자", example = "2025-02-28") @NotNull LocalDate endDate,
            @Schema(description = "루틴 반복 주기", example = "DAILY") @NotNull String cycle,
            @Schema(description = "루틴 반복 패턴", example = "[1, 2, 3]") @Null Set<Integer> pattern,
            @Schema(description = "루틴 반복 휴일 제외 여부", example = "false") @NotNull Boolean isExcludeHolidays) {}
}
