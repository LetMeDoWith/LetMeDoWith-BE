package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskRoutineExcludeDates {

    private Set<LocalDate> dates;

    public static TaskRoutineExcludeDates from(Set<LocalDate> dates) {
        return new TaskRoutineExcludeDates(
                dates.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new)));
    }
}
