package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record RegisterTodoTaskCommand(
    Long taskCategoryId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    Boolean isRoutine,
    TodoTaskRoutineCondition routineCondition
) {
    
    @Builder
    @Schema(description = "투두 태스크 루틴 조건 정보")
    public record TodoTaskRoutineCondition(
        @Schema(description = "루틴 반복 주기 (DAILY: 매일, WEEKLY: 매주, MONTHLY: 매월)", example = "WEEKLY")
        TodoTaskRoutineCycle cycle,
        
        @Schema(description = "루틴 패턴 (DAILY: 사용 안함, WEEKLY: 1-7 요일, MONTHLY: 1-31 일자, 99: 마지막일)", example = "[1, 3, 5]")
        Set<Integer> pattern,
        
        @Schema(description = "공휴일 제외 여부", example = "true")
        Boolean isExcludeHolidays
    ) {
    
    }
}