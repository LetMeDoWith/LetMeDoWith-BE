package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoTaskRoutineDateCalculatorTest {

    @Mock
    private TodoTaskRoutineScheduleStrategy mockStrategy;

    private TodoTaskRoutineDateCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TodoTaskRoutineDateCalculator();
    }

    @Test
    @DisplayName("[SUCCESS] 루틴 수행 일자 계산 성공")
    void testComputeRoutineDatesSuccess() {
        // given
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);
        Set<Integer> repetitionPattern = Set.of(1, 3, 5);
        Set<LocalDate> expectedDates = Set.of(
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 3),
                LocalDate.of(2024, 3, 5));

        when(mockStrategy.getRoutineDates(any(), any(), any())).thenReturn(expectedDates);

        // when
        Set<LocalDate> result = calculator.computeRoutineDates(mockStrategy, startDate, endDate, repetitionPattern);

        // then
        assertThat(result).isEqualTo(expectedDates);
    }

    @Test
    @DisplayName("[FAIL] 시작일이 종료일보다 늦은 경우 예외 발생")
    void testComputeRoutineDatesFailWhenStartDateIsAfterEndDate() {
        // given
        LocalDate startDate = LocalDate.of(2024, 3, 31);
        LocalDate endDate = LocalDate.of(2024, 3, 1);
        Set<Integer> repetitionPattern = Set.of(1, 3, 5);

        // when & then
        assertThatThrownBy(() -> calculator.computeRoutineDates(mockStrategy, startDate, endDate, repetitionPattern))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("status", FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
    }
}