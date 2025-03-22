package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.domain.converter.DowithTaskRoutineDatesConverter;
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
@Table(name = "dowith_task_routine")
public class DowithTaskRoutine extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "dates", columnDefinition = "TEXT")
    @Convert(converter = DowithTaskRoutineDatesConverter.class)
    private DowithTaskRoutineDates routineDates;
    
    public static DowithTaskRoutine from(Set<LocalDate> dates) {
        return DowithTaskRoutine.builder()
                                .routineDates(DowithTaskRoutineDates.from(dates))
                                .build();
    }
    
    public void updateRoutineDates(Set<LocalDate> dates) {
        this.routineDates = DowithTaskRoutineDates.from(dates);
    }
    
    public Set<LocalDate> getDates() {
        return this.routineDates.getDates();
    }
    
    public Set<LocalDate> getDatesBefore(LocalDate standardDate) {
        return this.routineDates.getDates().stream().filter(date -> date.isBefore(standardDate))
                                .collect(java.util.stream.Collectors.toSet());
    }
    
    public Set<LocalDate> getDatesAfter(LocalDate standardDate) {
        return this.routineDates.getDates().stream().filter(date -> date.isAfter(standardDate))
                                .collect(java.util.stream.Collectors.toSet());
    }
    
    public Set<LocalDate> getDatesAfterAndEqual(LocalDate standardDate) {
        return this.routineDates.getDates().stream().filter(date -> !date.isBefore(standardDate))
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
