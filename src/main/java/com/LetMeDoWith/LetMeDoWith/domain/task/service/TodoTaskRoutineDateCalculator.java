package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.strategy.TodoTaskRoutineDateCalculateStrategy;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 계산하는 서비스.
 */
@DomainService
@RequiredArgsConstructor
public class TodoTaskRoutineDateCalculator {
    
    private static final String ROUTINE_SCHEDULE_STRATEGY_KEY_SUFFIX = "RoutineScheduleStrategy";
    private final Map<String, TodoTaskRoutineDateCalculateStrategy> routineScheduleStrategies;
    
    /**
     * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 얻는다.
     *
     * @param cycle             루틴 반복 주기
     * @param startDate         루틴 시작 일자
     * @param endDate           루틴 종료 일자
     * @param repetitionPattern 루틴 반복 패턴
     * @return 루틴 수행 일자 목록
     */
    public Set<LocalDate> computeRoutineDates(TodoTaskRoutineCycle cycle,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              Set<Integer> repetitionPattern) {
        if (startDate.isAfter(endDate)) {
            throw new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
        }
        
        TodoTaskRoutineDateCalculateStrategy strategy = getStrategy(cycle);
        return strategy.getRoutineDates(startDate, endDate, repetitionPattern);
    }
    
    private TodoTaskRoutineDateCalculateStrategy getStrategy(TodoTaskRoutineCycle cycle) {
        String strategyKey = cycle.name().toLowerCase() + ROUTINE_SCHEDULE_STRATEGY_KEY_SUFFIX;
        TodoTaskRoutineDateCalculateStrategy strategy = routineScheduleStrategies.get(strategyKey);
        
        if (strategy == null) {
            throw new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
        }
        
        return strategy;
    }
}