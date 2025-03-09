package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineRepetitionCycle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Schema(description = "투두모드 Task 생성 요청")
public record CreateTodoTaskReqDto(
    @Schema(description = "제목", defaultValue = "아침 먹기")
    @NotBlank @Size(max = 40) String title,
    
    @Schema(description = "Task 카테고리 ID", defaultValue = "1")
    @NotBlank Long taskCategoryId,
    
    @Schema(description = "시작 일자", defaultValue = "2025-01-30")
    @NotBlank LocalDate startDate,
    
    @Schema(description = "시작 시각", defaultValue = "11:30:00")
    @NotBlank LocalTime startTime,
    
    @Schema(description = "종료 일자", defaultValue = "2025-01-30")
    LocalDate endDate,
    
    @Schema(description = "루틴 등록 여부", defaultValue = "true")
    @NotNull Boolean isRoutine,
    
    @Schema(description = "루틴 반복 주기", defaultValue = "DAILY")
    TodoTaskRoutineRepetitionCycle routineRepetitionCycle,
    
    @Schema(description = "루틴 반복 패턴", defaultValue = "[1, 2, 3]")
    Set<Integer> repetitionPattern

) {
    
    public CreateTodoTaskCommand toCreateTodoTaskCommand() {
        return CreateTodoTaskCommand.builder()
                                    .title(this.title)
                                    .taskCategoryId(this.taskCategoryId)
                                    .startDate(this.startDate)
                                    .startTime(this.startTime)
                                    .endDate(this.endDate)
                                    .isRoutine(this.isRoutine)
                                    .routineRepetitionCycle(this.routineRepetitionCycle)
                                    .repetitionPattern(this.repetitionPattern)
                                    .build();
    }
    
}