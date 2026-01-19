package com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;

import java.time.LocalDate;
import java.time.LocalTime;

public record DowithTaskQueryDto(
        Long id, // TODO - 추후 PK 정책에 따른 수정 필요
        Long taskCategoryId,
        String taskCategoryName,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        String successImageUrl,
        boolean isRoutine,
        int feedBackCount) {

    public DowithTaskQueryDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            DowithTaskStatus status,
            LocalDate date,
            LocalTime startTime,
            String successImageUrl,
            DowithTaskRoutine dowithTaskRoutine,
            int feedBackCount) {
        this(
                id,
                taskCategoryId,
                taskCategoryName,
                title,
                status.code,
                date,
                startTime,
                successImageUrl,
                dowithTaskRoutine != null,
                feedBackCount);
    }
}
