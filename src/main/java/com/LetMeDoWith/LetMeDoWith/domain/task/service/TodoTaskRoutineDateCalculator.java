package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;

/**
 * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 계산하는 서비스.
 */
@DomainService
@RequiredArgsConstructor
public class TodoTaskRoutineDateCalculator {
    
    /**
     * 루틴 반복 주기와 패턴에 따라 루틴 수행 일자 목록을 얻는다.
     *
     * @param strategy          루틴 반복 주기에 따른 루틴 수행 일자 목록을 얻는 전략
     * @param startDate         루틴 시작 일자
     * @param endDate           루틴 종료 일자
     * @param repetitionPattern 루틴 반복 패턴
     * @return 루틴 수행 일자 목록
     */
    public Set<LocalDate> computeRoutineDates(TodoTaskRoutineScheduleStrategy strategy,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              Set<Integer> repetitionPattern) {
        if (startDate.isAfter(endDate)) {
            throw new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
        }
        
        return strategy.getRoutineDates(startDate, endDate, repetitionPattern);
    }
    
}