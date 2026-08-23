package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateDowithTaskWithRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskRoutineCondition;
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

@Schema(description = "두윗모드 Task 생성 요청")
public record CreateDowithTaskReqDto(
        @Schema(description = "제목", example = "아침 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "Task 카테고리 ID", example = "1") Long taskCategoryId,
        @Schema(description = "시작 일자", example = "2025-01-30") @NotNull LocalDate date,
        @Schema(description = "시작 시각", example = "11:30:00") LocalTime startTime,
        @Schema(description = "루틴 반복 조건") CreateDowithTaskRoutineCondition routineCondition) {

    public CreateDowithTaskCommand toCreateDowithTaskCommand() {
        return CreateDowithTaskCommand.builder()
                .title(this.title)
                .taskCategoryId(this.taskCategoryId)
                .date(this.date)
                .startTime(this.startTime)
                .build();
    }

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
            @Schema(description = "시작 일자", example = "2025-01-30") @NotNull LocalDate startDate,
            @Schema(description = "종료 일자", example = "2025-02-28") @NotNull LocalDate endDate,
            @Schema(description = "루틴 반복 주기", implementation = TaskRoutineCycle.class, example = "DAILY") @NotNull
                    String cycle,
            @Schema(description = "루틴 반복 패턴", example = "[1, 2, 3]") @Null Set<Integer> pattern,
            @Schema(description = "루틴 반복 휴일 제외 여부", example = "false") @NotNull Boolean isExcludeHolidays) {}
}
