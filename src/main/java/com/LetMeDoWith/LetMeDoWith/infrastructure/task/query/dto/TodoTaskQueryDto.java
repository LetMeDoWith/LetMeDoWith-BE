package com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record TodoTaskQueryDto(
        Long id, // TODO - 추후 PK 정책에 따른 수정 필요
        Long taskCategoryId,
        String taskCategoryName,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime) {

    public TodoTaskQueryDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            TodoTaskStatus status,
            LocalDate date,
            LocalTime startTime) {
        this(id, taskCategoryId, taskCategoryName, title, status.code, date, startTime);
    }
}
