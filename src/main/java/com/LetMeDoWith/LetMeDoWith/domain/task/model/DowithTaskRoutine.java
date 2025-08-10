package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
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

    @Column(name = "is_exclude_holidays")
    private boolean isExcludeHolidays;

    public static DowithTaskRoutine of(LocalDate rangeStartDate, LocalDate rangeEndDate,
                                       TaskRoutineCycle cycle, Set<Integer> pattern, boolean isExcludeHolidays) {
        if (rangeStartDate.isBefore(SystemTimeUtil.nowDate()) || rangeEndDate.isBefore(SystemTimeUtil.nowDate()) || rangeEndDate.isBefore(rangeStartDate)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
        return DowithTaskRoutine.builder()
                .rangeStartDate(rangeStartDate)
                .rangeEndDate(rangeEndDate)
                .cycle(cycle)
                .pattern(TaskRoutinePattern.from(pattern))
                .isExcludeHolidays(isExcludeHolidays)
                .build();
    }

//    public static DowithTaskRoutine from(Set<LocalDate> dates) {
//
//        DowithTaskRoutineDates routineDates = DowithTaskRoutineDates.from(dates);
//        routineDates.validate();
//
//        return DowithTaskRoutine.builder().routineDates(routineDates).build();
//    }

    public void updateRoutineDates(Set<LocalDate> dates) {
        DowithTaskRoutineDates routineDate = DowithTaskRoutineDates.from(dates);
        routineDates.validate();
        this.routineDates = routineDate;
    }


    public Set<LocalDate> getDates() {
        return this.routineDates.getDates();
    }

    public Set<LocalDate> getDatesBeforeAndEqual(LocalDate standardDate) {
        return this.routineDates.getDates().stream()
                .filter(date -> !date.isAfter(standardDate))
                .collect(java.util.stream.Collectors.toSet());
    }

    public Set<LocalDate> getDatesAfter(LocalDate standardDate) {
        return this.routineDates.getDates().stream()
                .filter(date -> date.isAfter(standardDate))
                .collect(java.util.stream.Collectors.toSet());
    }

    public Set<LocalDate> getDatesAfterAndEqual(LocalDate standardDate) {
        return this.routineDates.getDates().stream()
                .filter(date -> !date.isBefore(standardDate))
                .collect(java.util.stream.Collectors.toSet());
    }

    public void addDates(Set<LocalDate> dates) {
        this.routineDates.getDates().addAll(dates);
    }

    public void deleteDate(LocalDate date) {
        this.routineDates.getDates().remove(date);
    }

    public void deleteDates(Set<LocalDate> dates) {
        this.routineDates.getDates().removeAll(dates);
    }
}
