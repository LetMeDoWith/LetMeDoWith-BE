package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "투두모드 Task 생성 응답 (루틴 생성 시 다건 포함)")
@Builder
public record CreateTodoTaskResDto(
        @Schema(description = "생성된 투두 Task 목록") List<TodoTaskVO> todoTaskList,
        @Schema(description = "루틴으로 생성된 수행 일자 집합") Set<LocalDate> routineDates) {

    public static CreateTodoTaskResDto of(List<TodoTaskVO> todoTaskList, Set<LocalDate> routineDates) {
        return CreateTodoTaskResDto.builder()
                .todoTaskList(todoTaskList)
                .routineDates(routineDates)
                .build();
    }

    @Schema(description = "생성된 투두 Task 요약")
    @Builder
    @Getter
    public static class TodoTaskVO {

        @Schema(description = "Task ID", example = "1")
        private final Long id;

        @Schema(description = "Task 카테고리 ID", example = "2")
        private final Long taskCategoryId;

        @Schema(description = "제목", example = "아침 먹기")
        private final String title;

        @Schema(description = "수행 일자", example = "2026-01-30")
        private final LocalDate date;

        @Schema(description = "시작 시각", example = "09:00:00")
        private final LocalTime startTime;

        @Schema(description = "루틴 Task 여부", example = "true")
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
}
