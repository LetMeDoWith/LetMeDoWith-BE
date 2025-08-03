package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateTodoTaskReqDto;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record RegisterTodoTaskCommand(
        Long taskCategoryId,
        String title,
        LocalDate date,
        LocalTime startTime,
        TodoTaskRoutineCondition routineCondition) {

    public static RegisterTodoTaskCommand of(
            @Nullable Long taskCategoryId,
            String title,
            LocalDate date,
            @Nullable LocalTime startTime,
            @Nullable CreateTodoTaskReqDto.TodoTaskRoutineCondition routineCondition) {
        return RegisterTodoTaskCommand.builder()
                .taskCategoryId(taskCategoryId)
                .title(title)
                .date(date)
                .startTime(startTime)
                .routineCondition(
                        routineCondition == null
                                ? null
                                : TodoTaskRoutineCondition.of(
                                        routineCondition.startDate(),
                                        routineCondition.endDate(),
                                        routineCondition.cycle(),
                                        routineCondition.pattern(),
                                        routineCondition.isExcludeHolidays()))
                .build();
    }
}
