package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdateDowithTaskRoutineReqDto(
        @Schema(description = "두윗모드 Task 루틴 일자 리스트", defaultValue = "[\"2025-04-01\", \"2025-04-02\"]")
                @NotNull
                List<LocalDate> routineDates) {}
