package com.LetMeDoWith.LetMeDoWith.domain.task.service.strategy;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;

import java.time.LocalDate;
import java.util.Set;

public interface TaskRoutineDateCalculateStrategy {

    Set<LocalDate> getRoutineDates(LocalDate startDate, LocalDate endDate, Set<Integer> repetitionPattern);

    TaskRoutineCycle getRoutineCycle();
}
