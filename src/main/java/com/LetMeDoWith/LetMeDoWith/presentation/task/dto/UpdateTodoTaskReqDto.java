package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.TodoTaskRoutineCondition;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateTodoTaskReqDto(
    String title,
    LocalTime startTime,
    Long taskCategoryId,
    TodoTaskRoutineCondition routineCondition
) {

}