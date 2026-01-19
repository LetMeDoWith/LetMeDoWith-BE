package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto.DowithTaskDetailQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Builder
public record RetrieveDowithTaskResult(
        @Schema(description = "Task ID", defaultValue = "1") Long id,
        @Schema(description = "Task 카테고리 ID", defaultValue = "1") Long taskCategoryId,
        @Schema(description = "Task 카테고리명", defaultValue = "일상") String taskCategoryName,
        @Schema(description = "제목", defaultValue = "아침 먹기") String title,
        @Schema(description = "상태", defaultValue = "WAIT") String status,
        @Schema(description = "시작 일자", defaultValue = "2025-01-30") LocalDate date,
        @Schema(description = "시작 시각", defaultValue = "11:30:00") LocalTime startTime,
        @Schema(description = "인증 사진", defaultValue = "[\"https:image\"]") List<String> successImageUrls,
        @Schema(description = "루틴 반복 조건") DowithTaskRoutine routine,
        @Schema(description = "잔소리 개수", defaultValue = "102") int feedBackCount) {
    public static RetrieveDowithTaskResult from(DowithTaskDetailQueryDto dto) {
        return RetrieveDowithTaskResult.builder()
                .id(dto.id())
                .taskCategoryId(dto.taskCategoryId())
                .taskCategoryName(dto.taskCategoryName())
                .title(dto.title())
                .status(dto.status())
                .date(dto.date())
                .startTime(dto.startTime())
                .successImageUrls(dto.successImageUrl() != null ? List.of(dto.successImageUrl()) : null)
                .routine(
                        dto.routineId() != null
                                ? DowithTaskRoutine.builder()
                                .startDate(dto.startDate())
                                .endDate(dto.endDate())
                                .cycle(dto.cycle())
                                .pattern(dto.patterns())
                                .isExcludeHolidays(dto.isExcludeHolidays())
                                .build()
                                : null)
                .feedBackCount(dto.feedBackCount())
                .build();
    }

    @Builder
    public record DowithTaskRoutine(
            @Schema(description = "시작 일자", defaultValue = "2025-01-30") @NotNull LocalDate startDate,
            @Schema(description = "종료 일자", defaultValue = "2025-01-30") @NotNull LocalDate endDate,
            @Schema(description = "루틴 반복 주기", defaultValue = "DAILY") @NotNull String cycle,
            @Schema(description = "루틴 반복 패턴", defaultValue = "[1, 2, 3]") @Null Set<Integer> pattern,
            @Schema(description = "루틴 반복 휴일 제외 여부", defaultValue = "false") @NotNull Boolean isExcludeHolidays) {
    }
}
