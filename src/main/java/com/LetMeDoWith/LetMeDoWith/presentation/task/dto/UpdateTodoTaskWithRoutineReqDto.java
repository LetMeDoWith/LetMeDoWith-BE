package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateTodoTaskWithRoutineCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateTodoTaskWithRoutineReqDto(
        @Schema(description = "제목", example = "저녁 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "시작 시간", example = "11:30:00") LocalTime startTime,
        @Schema(description = "Task 카테고리 ID", example = "2") Long taskCategoryId) {

    public UpdateTodoTaskWithRoutineCommand toCommand(Long todoTaskId) {
        return UpdateTodoTaskWithRoutineCommand.of(todoTaskId, this.title(), this.startTime(), this.taskCategoryId());
    }
}
