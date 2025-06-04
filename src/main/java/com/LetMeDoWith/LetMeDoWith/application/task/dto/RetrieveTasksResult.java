package com.LetMeDoWith.LetMeDoWith.application.task.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.query.dto.TodoTaskQueryDto;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        List<DowithTaskDto> dowithTaskDtos =
                dowithTaskQueryDtos.stream()
                        .collect(Collectors.groupingBy(DowithTaskQueryDto::id))
                        .values()
                        .stream()
                        .map(list -> {
                            DowithTaskQueryDto first = list.get(0);
                            List<String> confirmImageUrls = list.stream()
                                    .map(DowithTaskQueryDto::confirmedImageUrl)
                                    .filter(Objects::nonNull)
                                    .toList();
                            return new DowithTaskDto(
                                    first.id(),
                                    first.taskCategoryId(),
                                    first.taskCategoryName(),
                                    first.title(),
                                    first.status(),
                                    first.date(),
                                    first.startTime(),
                                    confirmImageUrls,
                                    first.feedBackCount());
                        })
                        .sorted(
                                (o1, o2) -> {
                                    int dateComparison = o1.date().compareTo(o2.date());
                                    if (dateComparison != 0) {
                                        return dateComparison;
                                    }
                                    return o1.startTime().compareTo(o2.startTime());
                                })
                        .toList();

        return new RetrieveTasksResult(todoTasks, dowithTaskDtos);
    }

    @Builder
    public record TodoTaskDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            String status,
            LocalDate date,
            LocalTime startTime) {
    }

    public record DowithTaskDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            String status,
            LocalDate date,
            LocalTime startTime,
            List<String> confirmedImageUrls,
            int feedBackCount) {
    }
}
