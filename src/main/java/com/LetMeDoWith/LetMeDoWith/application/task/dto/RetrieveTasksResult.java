package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskQueryDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

public record RetrieveTasksResult(List<TodoTaskDto> todoTasks, List<DowithTaskDto> dowithTasks) {

    public static RetrieveTasksResult of(
            List<TodoTaskQueryDto> todoTaskQueryDtos, List<DowithTaskQueryDto> dowithTaskQueryDtos) {
        List<TodoTaskDto> todoTasks =
                todoTaskQueryDtos.stream()
                        .map(
                                todoTaskQueryDto ->
                                        new TodoTaskDto(
                                                todoTaskQueryDto.id(),
                                                todoTaskQueryDto.taskCategoryId(),
                                                todoTaskQueryDto.taskCategoryName(),
                                                todoTaskQueryDto.title(),
                                                todoTaskQueryDto.status(),
                                                todoTaskQueryDto.date(),
                                                todoTaskQueryDto.startTime()))
                        .toList();

        List<DowithTaskDto> dowithTasks =
                dowithTaskQueryDtos.stream()
                        .map(
                                dowithTaskQueryDto ->
                                        new DowithTaskDto(
                                                dowithTaskQueryDto.id(),
                                                dowithTaskQueryDto.taskCategoryId(),
                                                dowithTaskQueryDto.taskCategoryName(),
                                                dowithTaskQueryDto.title(),
                                                dowithTaskQueryDto.status(),
                                                dowithTaskQueryDto.date(),
                                                dowithTaskQueryDto.startTime(),
                                                dowithTaskQueryDto.confirmedImageUrl(),
                                                dowithTaskQueryDto.feedBackCount()))
                        .toList();

        return new RetrieveTasksResult(todoTasks, dowithTasks);
    }

    @Builder
    public record TodoTaskDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            String status,
            LocalDate date,
            LocalTime startTime) {}

    public record DowithTaskDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            String status,
            LocalDate date,
            LocalTime startTime,
            String confirmedImageUrl,
            int feedBackCount) {}
}
