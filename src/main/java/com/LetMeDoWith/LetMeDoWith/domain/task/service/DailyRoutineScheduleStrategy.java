package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineRepetitionCycle;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@DomainService
public class DailyRoutineScheduleStrategy implements TodoTaskRoutineScheduleStrategy {
    
    /**
     * 매일 반복하는 투두 루틴의 루틴 수행일자 목록을 얻는다.
     *
     * @param startDate         루틴 시작 일자
     * @param endDate           루틴 종료 일자
     * @param repetitionPattern 루틴 반복 패턴 (Daily 의 경우 무시)
     * @return 루틴을 수행하는 일자 목록
     */
    @Override
    public Set<LocalDate> getRoutineDates(LocalDate startDate,
                                          LocalDate endDate,
                                          Set<Integer> repetitionPattern) {
        return startDate.datesUntil(endDate.plusDays(1))
                        .collect(Collectors.toCollection(TreeSet::new));
    }
    
    @Override
    public TodoTaskRoutineRepetitionCycle getRoutineCycle() {
        return TodoTaskRoutineRepetitionCycle.DAILY;
    }
}