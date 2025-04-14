package com.LetMeDoWith.LetMeDoWith.domain.task.service.strategy;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@DomainService
public class WeeklyRoutineDateCalculateStrategy implements TodoTaskRoutineDateCalculateStrategy {
    
    /**
     * 매주 반복하는 투두 루틴의 루틴 수행일자 목록을 얻는다.
     *
     * @param startDate         루틴 시작 일자
     * @param endDate           루틴 종료 일자
     * @param repetitionPattern 루틴 반복 패턴 (0: 일요일, 1: 월요일, ..., 6: 토요일)
     * @return 루틴을 수행하는 일자 목록
     */
    @Override
    public Set<LocalDate> getRoutineDates(LocalDate startDate,
                                          LocalDate endDate,
                                          Set<Integer> repetitionPattern) {
        Set<Integer> validDays = new TreeSet<>(repetitionPattern);
        
        return startDate.datesUntil(endDate.plusDays(1))
                        .filter(date -> validDays.contains(date.getDayOfWeek().getValue() - 1))
                        .collect(Collectors.toCollection(TreeSet::new));
    }
    
    @Override
    public TodoTaskRoutineCycle getRoutineCycle() {
        return TodoTaskRoutineCycle.WEEKLY;
    }
}