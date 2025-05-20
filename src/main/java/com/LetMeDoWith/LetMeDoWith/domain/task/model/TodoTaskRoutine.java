package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.converter.task.TodoTaskRooutineCycleConverter;
import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter.TodoTaskRoutineDatesConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    
    @Column(name = "dates", columnDefinition = "TEXT")
    @Convert(converter = TodoTaskRoutineDatesConverter.class)
    private TodoTaskRoutineDates routineDates;
    
    @Column(name = "cycle", nullable = false, length = 20)
    @Convert(converter = TodoTaskRooutineCycleConverter.class)
    private TodoTaskRoutineCycle cycle;
    
    @Column(name = "pattern")
    private TodoTaskRoutinePattern pattern;
    
    @Column(name = "is_exclude_holidays")
    private boolean isExcludeHolidays;
    
    public static TodoTaskRoutine of(Set<LocalDate> dates) {
        return TodoTaskRoutine.builder().routineDates(TodoTaskRoutineDates.from(dates)).build();
    }
    
    public static TodoTaskRoutine of(
        Set<LocalDate> dates,
        TodoTaskRoutineCycle cycle,
        Set<Integer> pattern,
        boolean isExcludeHolidays) {
        return TodoTaskRoutine.builder()
                              .routineDates(TodoTaskRoutineDates.from(dates))
                              .cycle(cycle)
                              .pattern(TodoTaskRoutinePattern.from(pattern))
                              .isExcludeHolidays(isExcludeHolidays)
                              .build();
    }
    
    public void updateRoutineDates(Set<LocalDate> dates) {
        this.routineDates = TodoTaskRoutineDates.from(dates);
    }
    
    public void removeRoutineDate(LocalDate date) {
        this.routineDates.removeDate(date);
    }
    
    public void removeRoutineDates(Set<LocalDate> dates) {
        this.routineDates.removeDates(dates);
    }
    
    public Set<LocalDate> getDates() {
        return this.routineDates.getDates();
    }
    
    public Set<LocalDate> getDatesBefore(LocalDate standardDate) {
        return this.routineDates.getDates().stream()
                                .filter(date -> date.isBefore(standardDate))
                                .collect(java.util.stream.Collectors.toSet());
    }
    
    public Set<LocalDate> getDatesAfterAndEqual(LocalDate standardDate) {
        return this.routineDates.getDates().stream()
                                .filter(date -> !date.isBefore(standardDate))
                                .collect(java.util.stream.Collectors.toSet());
    }
}