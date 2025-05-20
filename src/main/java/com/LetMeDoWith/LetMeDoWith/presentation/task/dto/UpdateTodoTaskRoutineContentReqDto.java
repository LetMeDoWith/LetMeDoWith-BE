package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateTodoTaskRoutineContentReqDto(
    String title,
    LocalTime startTime,
    Long taskCategoryId,
    Boolean isApplyToAll
) {

}