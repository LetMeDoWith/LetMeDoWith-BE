package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "투두 태스크 루틴 조건 정보")
public record UpdateTodoTaskRoutineReqDto(
    @Schema(description = "루틴 시작 일자", example = "2025-01-30")
    LocalDate startDate,
    @Schema(description = "루틴 종료 일자", example = "2025-01-30")
    LocalDate endDate,
    @Schema(description = "루틴 반복 주기 (DAILY: 매일, WEEKLY: 매주, MONTHLY: 매월)", example = "WEEKLY")
    TodoTaskRoutineCycle cycle,
    @Schema(description = "루틴 패턴 (DAILY: 사용 안함, WEEKLY: 1-7 요일, MONTHLY: 1-31 일자, 99: 마지막일)",
        example = "[1, 3, 5]")
    Set<Integer> pattern,
    @Schema(description = "공휴일 제외 여부", example = "true")
    Boolean isExcludeHolidays) {
    
    public static UpdateTodoTaskRoutineReqDto of(
        LocalDate startDate,
        LocalDate endDate,
        TodoTaskRoutineCycle cycle,
        Set<Integer> pattern,
        Boolean isExcludeHolidays) {
        return new UpdateTodoTaskRoutineReqDto(startDate,
                                               endDate,
                                               cycle,
                                               pattern,
                                               isExcludeHolidays);
    }
    
}