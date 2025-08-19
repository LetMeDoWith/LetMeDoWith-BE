package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter.TaskRooutineCycleConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "dowith_task_routine")
public class DowithTaskRoutine extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //    @Column(name = "dates", columnDefinition = "TEXT")
    //    @Convert(converter = DowithTaskRoutineDatesConverter.class)
    //    private DowithTaskRoutineDates routineDates;
    @Column(name = "range_start_date", nullable = false)
    private LocalDate rangeStartDate;

    @Column(name = "range_end_date", nullable = false)
    private LocalDate rangeEndDate;

    @Column(name = "cycle", nullable = false, length = 20)
    @Convert(converter = TaskRooutineCycleConverter.class)
    private TaskRoutineCycle cycle;

    @Column(name = "pattern")
    private TaskRoutinePattern pattern;

    @Column(name = "exclude_holidays_yn")
    private Yn isExcludeHolidays;

    public static DowithTaskRoutine of(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            boolean isExcludeHolidays) {
        if (rangeStartDate.isBefore(SystemTimeUtil.nowDate())
                || rangeEndDate.isBefore(SystemTimeUtil.nowDate())
                || rangeEndDate.isBefore(rangeStartDate)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
        return DowithTaskRoutine.builder()
                .rangeStartDate(rangeStartDate)
                .rangeEndDate(rangeEndDate)
                .cycle(cycle)
                .pattern(TaskRoutinePattern.from(pattern))
                .isExcludeHolidays(isExcludeHolidays ? Yn.TRUE : Yn.FALSE)
                .build();
    }

    public void updateRoutineCondition(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            boolean isExcludeHolidays) {
        this.rangeStartDate = rangeStartDate;
        this.rangeEndDate = rangeEndDate;
        this.cycle = cycle;
        this.pattern = TaskRoutinePattern.from(pattern);
        this.isExcludeHolidays = isExcludeHolidays ? Yn.TRUE : Yn.FALSE;
    }

}
