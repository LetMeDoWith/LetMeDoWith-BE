package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TaskRoutineCondition;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskRoutineCommand;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record UpdateDowithTaskRoutineReqDto(
        @Schema(description = "루틴 시작 일자", example = "2025-01-30") LocalDate startDate,
        @Schema(description = "루틴 종료 일자", example = "2025-01-30") LocalDate endDate,
        @Schema(description = "루틴 반복 주기 (DAILY: 매일, WEEKLY: 매주, MONTHLY: 매월)", example = "WEEKLY")
        TaskRoutineCycle cycle,
        @Schema(description = "루틴 패턴 (DAILY: 사용 안함, WEEKLY: 1-7 요일, MONTHLY: 1-31 일자, 99: 마지막일)", example = "[1, 3, 5]")
        Set<Integer> pattern,
        @Schema(description = "공휴일 제외 여부", example = "true") Boolean isExcludeHolidays) {

    public UpdateDowithTaskRoutineCommand toCommand(Long dowithTaskId) {
        return UpdateDowithTaskRoutineCommand.builder()
                .dowithTaskId(dowithTaskId)
                .taskRoutineCondition(
                        TaskRoutineCondition.builder()
                                .startDate(this.startDate)
                                .endDate(this.endDate)
                                .cycle(this.cycle)
                                .pattern(this.pattern)
                                .isExcludeHolidays(this.isExcludeHolidays)
                                .build()
                )
                .build();
    }
}
