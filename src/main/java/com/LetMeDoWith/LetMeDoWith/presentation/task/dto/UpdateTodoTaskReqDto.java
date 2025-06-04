package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateTodoTaskReqDto(
    String title,
    LocalTime startTime,
    Long taskCategoryId,
    UpdateTodoTaskRoutineConditionReqDto routineCondition
) {

}