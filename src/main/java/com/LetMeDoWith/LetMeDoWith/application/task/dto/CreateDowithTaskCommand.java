package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record CreateDowithTaskCommand(String title, Long taskCategoryId, LocalDate date, LocalTime startTime) {}
