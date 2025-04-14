package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TodoTaskRoutineDateCalculatorTest {
    
    @Mock
    private Map<String, TodoTaskRoutineDateCalculateStrategy> routineScheduleStrategies;
    
    @Mock
    private TodoTaskRoutineDateCalculateStrategy dailyStrategy;
    
    @InjectMocks
    private TodoTaskRoutineDateCalculator routineDateCalculator;
    
    @Test
    @DisplayName("시작일이 종료일보다 늦은 경우 예외가 발생한다.")
    void computeRoutineDates_WhenStartDateIsAfterEndDate_ThrowsException() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 2);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        
        // when & then
        assertThatThrownBy(() -> routineDateCalculator.computeRoutineDates(
            TodoTaskRoutineCycle.DAILY,
            startDate,
            endDate,
            Set.of(1))).isInstanceOf(RestApiException.class)
                       .hasFieldOrPropertyWithValue("status",
                                                    FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
    }
    
    @Test
    @DisplayName("지원하지 않는 루틴 주기인 경우 예외가 발생한다.")
    void computeRoutineDates_WhenUnsupportedCycle_ThrowsException() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);
        
        when(routineScheduleStrategies.get("dailyRoutineScheduleStrategy")).thenReturn(null);
        
        // when & then
        assertThatThrownBy(() -> routineDateCalculator.computeRoutineDates(
            TodoTaskRoutineCycle.DAILY,
            startDate,
            endDate,
            Set.of(1))).isInstanceOf(RestApiException.class)
                       .hasFieldOrPropertyWithValue("status",
                                                    FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
    }
    
    @Test
    @DisplayName("루틴 일정을 정상적으로 계산한다.")
    void computeRoutineDates_Success() {
        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);
        Set<LocalDate> expectedDates = Set.of(startDate, endDate);
        
        when(routineScheduleStrategies.get("dailyRoutineScheduleStrategy")).thenReturn(dailyStrategy);
        when(dailyStrategy.getRoutineDates(startDate,
                                           endDate,
                                           Set.of(1))).thenReturn(expectedDates);
        
        // when
        Set<LocalDate> actualDates = routineDateCalculator.computeRoutineDates(
            TodoTaskRoutineCycle.DAILY,
            startDate,
            endDate,
            Set.of(1));
        
        // then
        assertThat(actualDates).isEqualTo(expectedDates);
    }
}