package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TodoTaskVO {

    private final Long id;
    private final Long taskCategoryId;
    private final String title;
    private final LocalDate date;
    private final LocalTime startTime;
    private final Boolean isRoutine;

    public static TodoTaskVO from(TodoTask todoTask) {
        return TodoTaskVO.builder()
                .id(todoTask.getId())
                .taskCategoryId(todoTask.getTaskCategoryId())
                .title(todoTask.getTitle())
                .date(todoTask.getDate())
                .startTime(todoTask.getStartTime())
                .isRoutine(todoTask.getRoutine() != null)
                .build();
    }
}