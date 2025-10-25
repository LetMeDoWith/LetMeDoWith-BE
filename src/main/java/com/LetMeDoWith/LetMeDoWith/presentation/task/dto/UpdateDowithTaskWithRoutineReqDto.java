package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskWithRoutineCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateDowithTaskWithRoutineReqDto(
        @Schema(description = "제목", defaultValue = "저녁 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "시작 시간 (DowithTask가 이미 시작되었는데, 해당 일시 시 INVALID_REQUEST)", defaultValue = "11:30:00")
                @NotNull
                LocalTime startTime,
        @Schema(description = "Task 카테고리 ID", defaultValue = "2") Long taskCategoryId) {

    public UpdateDowithTaskWithRoutineCommand toCommand(Long dowithTaskId) {
        return UpdateDowithTaskWithRoutineCommand.builder()
                .dowithTaskId(dowithTaskId)
                .title(this.title)
                .taskCategoryId(this.taskCategoryId)
                .startTime(this.startTime)
                .build();
    }
}
