package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskDetailQueryDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record RetrieveTodoTaskResult(
        Long id,
        Long taskCategoryId,
        String taskCategoryName,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        TodoTaskRoutine routine) {

    public static RetrieveTodoTaskResult from(TodoTaskDetailQueryDto dto) {
        return RetrieveTodoTaskResult.builder()
                .id(dto.id())
                .taskCategoryId(dto.taskCategoryId())
                .taskCategoryName(dto.taskCategoryName())
                .title(dto.title())
                .status(dto.status())
                .date(dto.date())
                .startTime(dto.startTime())
                .routine(TodoTaskRoutine.builder()
                        .startDate(dto.startDate())
                        .endDate(dto.endDate())
                        .cycle(dto.cycle())
                        .pattern(dto.pattern())
                        .isExcludeHolidays(dto.isExcludeHolidays())
                        .build())
                .build();
    }

    @Builder
    public record TodoTaskRoutine(
            LocalDate startDate,
            LocalDate endDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            boolean isExcludeHolidays) {}
}
