package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import java.util.TreeSet;

@DomainService
public class MonthlyRoutineScheduleStrategy implements TodoTaskRoutineScheduleStrategy {
    
    
    /**
     * 매월 반복하는 투두 루틴의 루틴 수행일자 목록을 얻는다.
     *
     * @param startDate         루틴 시작일자
     * @param endDate           루틴 종료일자
     * @param repetitionPattern 루틴 반복 패턴 (1 ~ 31, 99(마지막 날))
     * @return 루틴을 수행하는 일자 목록
     */
    @Override
    public Set<LocalDate> getRoutineDates(LocalDate startDate,
                                          LocalDate endDate,
                                          Set<Integer> repetitionPattern) {
        // scheduleParams: 1~31, 99(마지막 날)
        Set<Integer> validDays = new TreeSet<>(repetitionPattern);
        Set<LocalDate> dates = new TreeSet<>();
        LocalDate date = startDate.withDayOfMonth(1);
        
        while (!date.isAfter(endDate)) {
            YearMonth ym = YearMonth.from(date);
            
            for (Integer day : validDays) {
                LocalDate scheduledDate;
                
                if (day == 99) {
                    scheduledDate = ym.atEndOfMonth();
                } else {
                    if (day > ym.lengthOfMonth()) {
                        continue;
                    }
                    scheduledDate = ym.atDay(day);
                }
                
                if (!scheduledDate.isBefore(startDate) && !scheduledDate.isAfter(endDate)) {
                    dates.add(scheduledDate);
                }
            }
            date = date.plusMonths(1).withDayOfMonth(1);
        }
        return dates;
    }
    
    @Override
    public TodoTaskRoutineCycle getRoutineCycle() {
        return TodoTaskRoutineCycle.MONTHLY;
    }
}