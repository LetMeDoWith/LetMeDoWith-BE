package com.LetMeDoWith.LetMeDoWith.domain.task.repository.dto;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskRoutinePattern;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record DowithTaskDetailQueryDto(
        Long id, // TODO - 추후 PK 정책에 따른 수정 필요
        Long taskCategoryId,
        String taskCategoryName,
        String title,
        String status,
        LocalDate date,
        LocalTime startTime,
        String successImageUrl,
        Long routineId,
        LocalDate startDate,
        LocalDate endDate,
        String cycle,
        Set<Integer> patterns,
        Boolean isExcludeHolidays,
        Long feedBackCount) {

    public DowithTaskDetailQueryDto(
            Long id,
            Long taskCategoryId,
            String taskCategoryName,
            String title,
            DowithTaskStatus status,
            LocalDate date,
            LocalTime startTime,
            String successImageUrl,
            Long routineId,
            LocalDate startDate,
            LocalDate endDate,
            TaskRoutineCycle cycle,
            TaskRoutinePattern pattern,
            Boolean isExcludeHolidays,
            Long feedBackCount) {
        this(
                id,
                taskCategoryId,
                taskCategoryName,
                title,
                status.getCode(),
                date,
                startTime,
                successImageUrl,
                routineId,
                startDate,
                endDate,
                cycle != null ? cycle.getCode() : null,
                !pattern.getPattern().isEmpty() ? pattern.getPattern() : null,
                isExcludeHolidays,
                feedBackCount);
    }

    //    public DowithTaskDetailQueryDto(
    //            DowithTask dowithTask,
    //            TaskCategory taskCategory,
    //            DowithTaskSuccess dowithTaskSuccess,
    //            DowithTaskRoutine routine,
    //            int feedBackCount) {
    //        this(
    //                dowithTask.getId(),
    //                taskCategory != null ? taskCategory.getId() : null,
    //                taskCategory != null ? taskCategory.getTitle() : null,
    //                dowithTask.getTitle(),
    //                dowithTask.getStatus().getCode(),
    //                dowithTask.getDate(),
    //                dowithTask.getStartTime(),
    //                dowithTaskSuccess != null ? dowithTaskSuccess.getImageUrl() : null,
    //                routine != null ? routine.getId() : null,
    //                routine != null ? routine.getRangeStartDate() : null,
    //                routine != null ? routine.getRangeEndDate() : null,
    //                routine != null ? routine.getCycle().getCode() : null,
    //                routine != null && routine.getPattern() != null
    //                        ? routine.getPattern().getPattern()
    //                        : null,
    //                routine != null ? routine.isExcludeHolidays() : null,
    //                feedBackCount);
    //    }
}
