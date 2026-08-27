package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateDowithTaskWithRoutineCommand(
        Long dowithTaskId, Long taskCategoryId, String title, LocalTime startTime) {}
