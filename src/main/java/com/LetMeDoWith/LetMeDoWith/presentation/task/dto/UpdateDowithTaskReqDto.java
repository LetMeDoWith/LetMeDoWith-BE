package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskCommand;
import com.LetMeDoWith.LetMeDoWith.common.util.EnumUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateDowithTaskReqDto(
        @Schema(description = "제목", defaultValue = "저녁 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "Task 카테고리 ID", defaultValue = "2") Long taskCategoryId,
        @Schema(description = "시작 일자 (DowithTask가 이미 시작되었는데, 해당 날짜 수정 시 INVALID_REQUEST)", defaultValue = "2025-01-30")
                @NotNull
                LocalDate date,
        @Schema(description = "시작 시각 (DowithTask가 이미 시작되었는데, 해당 시각 수정 시 INVALID_REQUEST)", defaultValue = "11:30:00")
                @NotNull
                LocalTime startTime,
        @Schema(description = "루틴 반복 조건") @Null UpdateDowithTaskRoutineCondition routineCondition) {

    public UpdateDowithTaskCommand toCommand(Long dowithTaskId) {
        return UpdateDowithTaskCommand.builder()
                .dowithTaskId(dowithTaskId)
                .title(this.title)
                .taskCategoryId(this.taskCategoryId)
                .date(this.date())
                .startTime(this.startTime())
                .taskRoutineCondition(
                        routineCondition == null
                                ? null
                                : TaskRoutineCondition.of(
                                        this.routineCondition.startDate,
                                        this.routineCondition.endDate,
                                        EnumUtil.getEnum(TaskRoutineCycle.class, this.routineCondition.cycle),
                                        this.routineCondition.pattern,
                                        this.routineCondition.isExcludeHolidays))
                .build();
    }

    public record UpdateDowithTaskRoutineCondition(
            @Schema(description = "시작 일자", defaultValue = "2025-01-30") @NotNull LocalDate startDate,
            @Schema(description = "종료 일자", defaultValue = "2025-01-30") @NotNull LocalDate endDate,
            @Schema(description = "루틴 반복 주기", defaultValue = "DAILY") @NotNull String cycle,
            @Schema(description = "루틴 반복 패턴", defaultValue = "[1, 2, 3]") @Null Set<Integer> pattern,
            @Schema(description = "루틴 반복 휴일 제외 여부", defaultValue = "false") @NotNull Boolean isExcludeHolidays) {}
}
