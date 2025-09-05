package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter.TaskRoutineCycleConverter;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter.TaskRoutinePatternConverter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "todo_task_routine")
public class TodoTaskRoutine extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "range_start_date", nullable = false)
    private LocalDate rangeStartDate;

    @Column(name = "range_end_date", nullable = false)
    private LocalDate rangeEndDate;

    @Column(name = "cycle", nullable = false, length = 20)
    @Convert(converter = TaskRoutineCycleConverter.class)
    private TaskRoutineCycle cycle;

    @Column(name = "pattern", columnDefinition = "TEXT")
    @Convert(converter = TaskRoutinePatternConverter.class)
    private TaskRoutinePattern pattern;

    @Column(name = "is_exclude_holidays")
    private boolean isExcludeHolidays;

    public static TodoTaskRoutine of(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            boolean isExcludeHolidays) {
        return TodoTaskRoutine.builder()
                .rangeStartDate(rangeStartDate)
                .rangeEndDate(rangeEndDate)
                .cycle(cycle)
                .pattern(TaskRoutinePattern.from(pattern))
                .isExcludeHolidays(isExcludeHolidays)
                .build();
    }

    public TodoTaskRoutine update(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle cycle,
            Set<Integer> pattern,
            boolean isExcludeHolidays) {
        this.rangeStartDate = rangeStartDate;
        this.rangeEndDate = rangeEndDate;
        this.cycle = cycle;
        this.pattern = TaskRoutinePattern.from(pattern);
        this.isExcludeHolidays = isExcludeHolidays;
        return this;
    }
}
