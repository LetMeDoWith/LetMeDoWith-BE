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

/** 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 계산하는 서비스. */
@DomainService
@RequiredArgsConstructor
public class TodoTaskRoutineDateCalculator {

    private static final String ROUTINE_SCHEDULE_STRATEGY_KEY_SUFFIX = "RoutineDateCalculateStrategy";
    private final Map<String, TodoTaskRoutineDateCalculateStrategy> routineScheduleStrategies;

    /**
     * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 얻는다.
     *
     * @param cycle 루틴 반복 주기
     * @param startDate 루틴 시작 일자
     * @param endDate 루틴 종료 일자
     * @param repetitionPattern 루틴 반복 패턴
     * @return 루틴 수행 일자 목록
     */
    public Set<LocalDate> computeRoutineDates(
            TodoTaskRoutineCycle cycle,
            LocalDate startDate,
            LocalDate endDate,
            Set<Integer> repetitionPattern) {
        if (startDate.isAfter(endDate)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        TodoTaskRoutineDateCalculateStrategy strategy = getStrategy(cycle);
        return strategy.getRoutineDates(startDate, endDate, repetitionPattern);
    }

    /**
     * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 얻고, 휴일을 제외한다.
     *
     * @param cycle 루틴 반복 주기
     * @param startDate 루틴 시작 일자
     * @param endDate 루틴 종료 일자
     * @param repetitionPattern 루틴 반복 패턴
     * @param holidays 휴일 목록
     * @return 휴일을 제외한 루틴 수행 일자 목록
     */
    public Set<LocalDate> computeRoutineDates(
            TodoTaskRoutineCycle cycle,
            LocalDate startDate,
            LocalDate endDate,
            Set<Integer> repetitionPattern,
            Set<LocalDate> holidays) {

        Set<LocalDate> computedRoutineDates =
                computeRoutineDates(cycle, startDate, endDate, repetitionPattern);

        computedRoutineDates.removeAll(holidays);
        return computedRoutineDates;
    }

    private TodoTaskRoutineDateCalculateStrategy getStrategy(TodoTaskRoutineCycle cycle) {
        String strategyKey = cycle.name().toLowerCase() + ROUTINE_SCHEDULE_STRATEGY_KEY_SUFFIX;
        TodoTaskRoutineDateCalculateStrategy strategy = routineScheduleStrategies.get(strategyKey);

        if (strategy == null) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        return strategy;
    }
}
