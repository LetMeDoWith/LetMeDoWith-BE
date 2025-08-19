package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import lombok.Builder;

@Builder
public record UpdateDowithTaskRoutineCommand(Long dowithTaskId, TaskRoutineCondition taskRoutineCondition) {}
