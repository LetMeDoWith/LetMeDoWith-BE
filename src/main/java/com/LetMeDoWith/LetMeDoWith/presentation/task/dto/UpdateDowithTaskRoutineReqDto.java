package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record UpdateDowithTaskRoutineReqDto(
    Long dowithTaskId,
    List<LocalDate> routineDates
) {

}
