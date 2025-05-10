package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "두윗모드 Task 생성 결과")
public record CreateDowithTaskResDto(
        @Schema(description = "생성된 두윗모드 Task 리스트") List<DowithTaskDto> dowithTaskDtos) {

    public static CreateDowithTaskResDto toCreateDowithTaskResDto(List<DowithTask> savedDowithTasks) {
        ArrayList<DowithTaskDto> dowithTaskDtos = new ArrayList<DowithTaskDto>();
        return new CreateDowithTaskResDto(savedDowithTasks.stream().map(DowithTaskDto::from).toList());
    }

    @Builder
    @Schema(description = "생성된 두윗모드 Task")
    public record DowithTaskDto(
            @Schema(description = "두윗모드Task ID", defaultValue = "1") Long id,
            @Schema(description = "Task카테고리 ID", defaultValue = "2") Long taskCategoryId,
            @Schema(description = "제목", defaultValue = "아침 먹기") String title,
            @Schema(description = "상태", implementation = DowithTaskStatus.class) DowithTaskStatus status,
            @Schema(description = "일자", defaultValue = "2025-01-30") LocalDate date,
            @Schema(description = "시작시간", defaultValue = "11:30:00") LocalTime startTime,
            @Schema(description = "루틴여부", defaultValue = "true") Boolean isRoutine,
            @Schema(description = "루틴일자", defaultValue = "[\"2025-02-01\", \"2025-02-02\"]")
                    List<LocalDate> routineDates) {

        public static DowithTaskDto from(DowithTask dowithTask) {
            return DowithTaskDto.builder()
                    .id(dowithTask.getId())
                    .taskCategoryId(dowithTask.getTaskCategoryId())
                    .title(dowithTask.getTitle())
                    .status(dowithTask.getStatus())
                    .date(dowithTask.getDate())
                    .startTime(dowithTask.getStartTime())
                    .isRoutine(dowithTask.isRoutine())
                    .routineDates(dowithTask.getRoutineDates().stream().toList())
                    .build();
        }
    }
}
