package com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskRoutinePattern;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record TodoTaskDetailQueryDto(
        Long id,
        Long taskCategoryId,
        String taskCategoryName,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        Long routineId,
        LocalDate startDate,
        LocalDate endDate,
        String cycle,
        Set<Integer> pattern,
        boolean isExcludeHolidays) {

    public TodoTaskDetailQueryDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            TodoTaskStatus status,
            LocalDate date,
            LocalTime startTime,
            Long routineId,
            LocalDate startDate,
            LocalDate endDate,
            TaskRoutineCycle cycle,
            TaskRoutinePattern pattern,
            boolean isExcludeHolidays) {
        this(
                id,
                taskCategoryId,
                taskCategoryName,
                title,
                status.code,
                date,
                startTime,
                routineId,
                startDate,
                endDate,
                cycle != null ? cycle.getCode() : null,
                !pattern.getPattern().isEmpty() ? pattern.getPattern() : null,
                isExcludeHolidays);
    }
}
