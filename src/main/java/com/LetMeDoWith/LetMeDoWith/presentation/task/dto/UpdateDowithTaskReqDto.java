package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.UpdateDowithTaskContentsCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record UpdateDowithTaskReqDto(
        @Schema(description = "제목", defaultValue = "저녁 먹기") @NotBlank @Size(max = 40) String title,
        @Schema(description = "Task 카테고리 ID", defaultValue = "2") Long taskCategoryId,
        @Schema(description = "시작일시", defaultValue = "2025-03-30T11:30:00") LocalDateTime startDateTime,
        @Schema(
                        description = "루틴일 (루틴 생성하지 않으면 null)",
                        defaultValue = "[\"2025-04-01\", \"2025-04-02\"]",
                        nullable = true)
                List<LocalDate> routineDates) {

    public UpdateDowithTaskContentsCommand toCommand() {
        return UpdateDowithTaskContentsCommand.builder()
                .title(this.title)
                .taskCategoryId(this.taskCategoryId)
                .date(this.startDateTime().toLocalDate())
                .startTime(this.startDateTime().toLocalTime())
                .build();
    }

    public Set<LocalDate> getRoutineDates() {
        if (routineDates == null) {
            return null;
        }
        Set<LocalDate> routineDates = new HashSet<>(this.routineDates);
        routineDates.add(startDateTime.toLocalDate());
        return routineDates;
    }
}
