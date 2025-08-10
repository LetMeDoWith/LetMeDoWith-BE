package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.strategy.TaskRoutineDateCalculateStrategy;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 계산하는 서비스.
 */
@DomainService
@RequiredArgsConstructor
public class TaskRoutineDateCalculator {

    private static final String ROUTINE_SCHEDULE_STRATEGY_KEY_SUFFIX = "RoutineDateCalculateStrategy";
    private final Map<String, TaskRoutineDateCalculateStrategy> routineScheduleStrategies;

    public Set<LocalDate> calculateRoutineDates(DowithTask dowithTask, List<Holiday> holidays) {
        if (!dowithTask.isRoutine()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        DowithTaskRoutine routine = dowithTask.getRoutine();
        TaskRoutineDateCalculateStrategy strategy = this.getStrategy(routine.getCycle());
        Set<LocalDate> dates = strategy.getRoutineDates(
                routine.getRangeStartDate(),
                routine.getRangeEndDate(),
                routine.getPattern().getPattern());
        if (routine.isExcludeHolidays()) {
            Set<LocalDate> holidayDates = holidays.stream().map(Holiday::getDate).collect(Collectors.toSet());
            dates.removeAll(holidayDates);
        }
        return dates;
    }

    /**
     * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 얻는다.
     *
     * @param cycle             루틴 반복 주기
     * @param startDate         루틴 시작 일자
     * @param endDate           루틴 종료 일자
     * @param repetitionPattern 루틴 반복 패턴
     * @return 루틴 수행 일자 목록
     */
    public Set<LocalDate> computeRoutineDates(
            TaskRoutineCycle cycle, LocalDate startDate, LocalDate endDate, Set<Integer> repetitionPattern) {
        if (startDate.isAfter(endDate)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        TaskRoutineDateCalculateStrategy strategy = getStrategy(cycle);
        return strategy.getRoutineDates(startDate, endDate, repetitionPattern);
    }

    /**
     * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 얻고, 휴일을 제외한다.
     *
     * @param cycle             루틴 반복 주기
     * @param startDate         루틴 시작 일자
     * @param endDate           루틴 종료 일자
     * @param repetitionPattern 루틴 반복 패턴
     * @param holidays          휴일 목록
     * @return 휴일을 제외한 루틴 수행 일자 목록
     */
    public Set<LocalDate> computeRoutineDates(
            TaskRoutineCycle cycle,
            LocalDate startDate,
            LocalDate endDate,
            Set<Integer> repetitionPattern,
            Set<LocalDate> holidays) {

        Set<LocalDate> computedRoutineDates = computeRoutineDates(cycle, startDate, endDate, repetitionPattern);

        computedRoutineDates.removeAll(holidays);
        return computedRoutineDates;
    }

    private TaskRoutineDateCalculateStrategy getStrategy(TaskRoutineCycle cycle) {
        String strategyKey = cycle.name().toLowerCase() + ROUTINE_SCHEDULE_STRATEGY_KEY_SUFFIX;
        TaskRoutineDateCalculateStrategy strategy = routineScheduleStrategies.get(strategyKey);

        if (strategy == null) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        return strategy;
    }
}
