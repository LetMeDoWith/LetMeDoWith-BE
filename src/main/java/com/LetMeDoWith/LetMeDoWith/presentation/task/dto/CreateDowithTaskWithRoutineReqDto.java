package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskWithRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.common.util.EnumUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record CreateDowithTaskWithRoutineReqDto(
        @Schema(description = "제목", defaultValue = "아침 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "Task 카테고리 ID", defaultValue = "1") Long taskCategoryId,
        @Schema(description = "시작 일자", defaultValue = "2025-01-30") @NotNull LocalDate date,
        @Schema(description = "시작 시각", defaultValue = "11:30:00") LocalTime startTime,
        @Schema(description = "루틴 반복 조건") CreateDowithTaskRoutineCondition routineCondition) {

    public CreateDowithTaskWithRoutineCommand toCreateDowithTaskWithRoutineCommand() {
        return CreateDowithTaskWithRoutineCommand.builder()
                .taskCategoryId(this.taskCategoryId)
                .title(this.title)
                .date(this.date)
                .startTime(this.startTime)
                .routineCondition(TaskRoutineCondition.builder()
                        .startDate(this.routineCondition.startDate)
                        .endDate(this.routineCondition.endDate)
                        .cycle(EnumUtil.getEnum(TaskRoutineCycle.class, this.routineCondition.cycle))
                        .pattern(this.routineCondition.pattern)
                        .isExcludeHolidays(this.routineCondition.isExcludeHolidays)
                        .build())
                .build();
    }

    public record CreateDowithTaskRoutineCondition(
            @Schema(description = "시작 일자", defaultValue = "2025-01-30") @NotNull LocalDate startDate,
            @Schema(description = "종료 일자", defaultValue = "2025-01-30") @NotNull LocalDate endDate,
            @Schema(description = "루틴 반복 주기", defaultValue = "DAILY") @NotNull String cycle,
            @Schema(description = "루틴 반복 패턴", defaultValue = "[1, 2, 3]") @NotNull Set<Integer> pattern,
            @Schema(description = "루틴 반복 휴일 제외 여부", defaultValue = "false") @NotNull Boolean isExcludeHolidays
    ) {
    }

}
