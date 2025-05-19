package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.TodoTaskRoutineCondition;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Schema(description = "투두모드 Task 생성 요청")
public record CreateTodoTaskReqDto(
    @Schema(description = "제목", defaultValue = "아침 먹기") @NotBlank @Size(max = 40) String title,
    @Schema(description = "Task 카테고리 ID", defaultValue = "1") @NotNull Long taskCategoryId,
    @Schema(description = "시작 일자", defaultValue = "2025-01-30") @NotNull LocalDate date,
    @Schema(description = "시작 시각", defaultValue = "11:30:00") @NotNull LocalTime startTime,
    @Schema(description = "루틴 반복 정보") Optional<TodoTaskRoutineCondition> routineCondition) {
    
    public RegisterTodoTaskCommand toCreateTodoTaskCommand() {
        return RegisterTodoTaskCommand.builder()
                                      .title(this.title)
                                      .taskCategoryId(this.taskCategoryId)
                                      .date(this.date)
                                      .startTime(this.startTime)
                                      .routineCondition(this.routineCondition)
                                      .build();
    }
}