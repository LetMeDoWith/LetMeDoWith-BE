package com.LetMeDoWith.LetMeDoWith.domain.task.service.strategy;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import java.time.LocalDate;
import java.util.Set;

public interface TodoTaskRoutineDateCalculateStrategy {

    Set<LocalDate> getRoutineDates(
            LocalDate startDate, LocalDate endDate, Set<Integer> repetitionPattern);

    TodoTaskRoutineCycle getRoutineCycle();
}
