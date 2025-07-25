package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UpdateTodoTaskWithRoutineReqDto(String title, LocalDateTime startDateTime, Long taskCategoryId) {}
