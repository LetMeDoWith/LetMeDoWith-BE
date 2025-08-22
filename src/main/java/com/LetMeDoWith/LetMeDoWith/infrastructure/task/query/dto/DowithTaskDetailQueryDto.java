package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record DowithTaskDetailQueryDto(
        Long id, // TODO - 추후 PK 정책에 따른 수정 필요
        Long taskCategoryId,
        String taskCategoryName,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        String confirmedImageUrl,
        LocalDate startDate,
        LocalDate endDate,
        String cycle,
        Set<Integer> patterns,
        boolean isExcludeHolidays,
        int feedBackCount) {

    public DowithTaskDetailQueryDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            DowithTaskStatus status,
            LocalDate date,
            LocalTime startTime,
            String confirmedImageUrl,
            LocalDate startDate,
            LocalDate endDate,
            String cycle,
            Set<Integer> patterns,
            boolean isExcludeHolidays,
            int feedBackCount) {
        this(
                id,
                taskCategoryId,
                taskCategoryName,
                title,
                status.code,
                date,
                startTime,
                confirmedImageUrl,
                startDate,
                endDate,
                cycle,
                patterns,
                isExcludeHolidays,
                feedBackCount);
    }
}
