package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.*;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.strategy.TaskRoutineDateCalculateStrategy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

/**
 * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 계산하는 서비스.
 */
@DomainService
@RequiredArgsConstructor
public class TaskRoutineDateCalculator {

    private static final String ROUTINE_SCHEDULE_STRATEGY_KEY_SUFFIX = "RoutineDateCalculateStrategy";
    private final Map<String, TaskRoutineDateCalculateStrategy> routineScheduleStrategies;

    public static Predicate<LocalDate> isEditableDatePredicate(
            LocalDate nowDate, LocalTime nowTime, LocalTime startTime) {
        return date -> !date.isBefore(nowDate) || (date.isEqual(nowDate) && nowTime.isBefore(startTime));
    }

    /**
     * DowithTask의 루틴 일자를 계산
     *
     * @param dowithTask
     * @param holidaySet
     * @return
     */
    public Set<LocalDate> calculateRoutineDates(DowithTask dowithTask, Set<Holiday> holidaySet) {
        if (!dowithTask.isRoutine()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        DowithTaskRoutine routine = dowithTask.getRoutine();
        return this.calculateRoutineDates(
                routine.getCycle(),
                routine.getRangeStartDate(),
                routine.getRangeEndDate(),
                routine.getPattern() != null ? routine.getPattern().getPattern() : null,
                Yn.TRUE.equals(routine.getIsExcludeHolidays()),
                holidaySet.stream().map(Holiday::getDate).collect(Collectors.toSet()));
    }

    /**
     * TodoTask의 루틴 일자를 계산
     *
     * @param todoTask
     * @param holidaySet
     * @return
     */
    public Set<LocalDate> calculateRoutineDates(TodoTask todoTask, Set<Holiday> holidaySet) {
        if (!todoTask.isRoutine()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        TodoTaskRoutine routine = todoTask.getRoutine();
        // TODO - todoTask refact시에 아래 private method 사용
        return null;
    }

    /**
     * 수정 가능한 Routine Dates 계산
     *
     * @param dowithTask
     * @param holidaySet
     * @return
     */
    public Set<LocalDate> calculateEditableRoutineDates(DowithTask dowithTask, Set<Holiday> holidaySet) {
        if (!dowithTask.isRoutine()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        DowithTaskRoutine routine = dowithTask.getRoutine();
        Set<LocalDate> dates = this.calculateRoutineDates(
                routine.getCycle(),
                routine.getRangeStartDate(),
                routine.getRangeEndDate(),
                routine.getPattern().getPattern(),
                Yn.TRUE.equals(routine.getIsExcludeHolidays()),
                holidaySet.stream().map(Holiday::getDate).collect(Collectors.toSet()));

        return dates.stream()
                .filter(isEditableDatePredicate(
                        SystemTimeUtil.nowDate(), SystemTimeUtil.nowTime(), dowithTask.getStartTime()))
                .collect(Collectors.toSet());
    }

    /**
     * 새 루틴 조건 대비 수정 대상 루틴 일자 계산
     *
     * @param dowithTask
     * @param newRoutineCondition
     * @param holidaySet
     * @return
     */
    public RoutineDateToModify calculateRoutineDatesToModify(
            DowithTask dowithTask, RoutineCondition newRoutineCondition, Set<Holiday> holidaySet) {
        LocalDateTime now = SystemTimeUtil.now();
        LocalDate nowDate = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();

        // 수정 가능한 루틴일자 계산 (시작 일시가 현재 일시보다 이후인 것들만)
        Set<LocalDate> existingRoutineDates = this.calculateRoutineDates(dowithTask, holidaySet).stream()
                .filter(isEditableDatePredicate(nowDate, nowTime, dowithTask.getStartTime()))
                .collect(Collectors.toSet());
        Set<LocalDate> newRoutineDates = this.calculateRoutineDates(
                newRoutineCondition.cycle(),
                newRoutineCondition.rangeStartDate(),
                newRoutineCondition.rangeEndDate(),
                newRoutineCondition.repetitionPattern(),
                newRoutineCondition.isExcludeHolidays(),
                holidaySet.stream()
                        .map(Holiday::getDate)
                        .filter(isEditableDatePredicate(nowDate, nowTime, dowithTask.getStartTime()))
                        .collect(Collectors.toSet()));

        // 삭제 대상인 루틴 일자 계산
        Set<LocalDate> toDeleteDates = new HashSet<>(existingRoutineDates);
        toDeleteDates.removeAll(newRoutineDates);

        // 새 루틴으로 수정 대상인 루틴 일자 계산
        Set<LocalDate> toUpdateDates = new HashSet<>(existingRoutineDates);
        toUpdateDates.retainAll(newRoutineDates);

        // 생성 대상인 루틴 일자 계산
        Set<LocalDate> toCreateDates = new HashSet<>(newRoutineDates);
        toCreateDates.removeAll(existingRoutineDates);

        return new RoutineDateToModify(toCreateDates, toUpdateDates, toDeleteDates);
    }

    private Set<LocalDate> calculateRoutineDates(
            TaskRoutineCycle cycle,
            LocalDate startDate,
            LocalDate endDate,
            Set<Integer> repetitionPattern,
            boolean isExcludeHolidays,
            Set<LocalDate> holidayDates) {
        if (startDate.isAfter(endDate)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        TaskRoutineDateCalculateStrategy strategy = getStrategy(cycle);
        Set<LocalDate> dates = strategy.getRoutineDates(startDate, endDate, repetitionPattern);
        if (isExcludeHolidays) {
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

    public record RoutineCondition(
            LocalDate rangeStartDate,
            LocalDate rangeEndDate,
            TaskRoutineCycle cycle,
            Set<Integer> repetitionPattern,
            boolean isExcludeHolidays) {}

    public record RoutineDateToModify(
            Set<LocalDate> toCreateDates, Set<LocalDate> toUpdateDates, Set<LocalDate> toDeleteDates) {}
}
