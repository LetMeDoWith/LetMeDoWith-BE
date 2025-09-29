package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record RetrieveTodoTaskResDto(
        Long id,
        Long taskCategoryId,
        String taskCategoryName,
        String title,
        LocalDate date,
        LocalTime startTime,
        TodoTaskRoutineDto routine) {

    public static RetrieveTodoTaskResDto from(RetrieveTodoTaskResult result) {
        return RetrieveTodoTaskResDto.builder()
                .id(result.id())
                .taskCategoryId(result.taskCategoryId())
                .taskCategoryName(result.taskCategoryName())
                .title(result.title())
                .date(result.date())
                .startTime(result.startTime())
                .routine(TodoTaskRoutineDto.builder()
                        .startDate(result.routine().startDate())
                        .endDate(result.routine().endDate())
                        .cycle(result.routine().cycle())
                        .pattern(result.routine().pattern())
                        .isExcludeHolidays(result.routine().isExcludeHolidays())
                        .build())
                .build();
    }

    @Builder
    public record TodoTaskRoutineDto(
            LocalDate startDate,
            LocalDate endDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            boolean isExcludeHolidays) {}
}
