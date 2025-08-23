package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
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
        LocalDate startDate,
        LocalDate endDate,
        String cycle,
        Set<Integer> patterns,
        boolean isExcludeHolidays) {
    public TodoTaskDetailQueryDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            TodoTaskStatus status,
            LocalDate date,
            LocalTime startTime,
            LocalDate startDate,
            LocalDate endDate,
            String cycle,
            Set<Integer> patterns,
            boolean isExcludeHolidays) {
        this(
                id,
                taskCategoryId,
                taskCategoryName,
                title,
                status.code,
                date,
                startTime,
                startDate,
                endDate,
                cycle,
                patterns,
                isExcludeHolidays);
    }
}
