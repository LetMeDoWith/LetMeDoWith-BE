package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HolidayFilterTest {

        @Mock
        private HolidayService holidayService;

        @InjectMocks
        private HolidayFilter holidayFilter;

        private LocalDate startDate;
        private LocalDate endDate;
        private Set<LocalDate> holidays;
        private Set<LocalDate> dates;

        @BeforeEach
        void setUp() {
                startDate = LocalDate.of(2024, 1, 1);
                endDate = LocalDate.of(2024, 12, 31);
                holidays = Set.of(
                                LocalDate.of(2024, 1, 1), // 신정
                                LocalDate.of(2024, 2, 9), // 설날
                                LocalDate.of(2024, 2, 10), // 설날
                                LocalDate.of(2024, 2, 11), // 설날
                                LocalDate.of(2024, 3, 1) // 삼일절
                );
                dates = Set.of(
                                LocalDate.of(2024, 1, 1), // 신정 (공휴일)
                                LocalDate.of(2024, 1, 2), // 일반일
                                LocalDate.of(2024, 2, 9), // 설날 (공휴일)
                                LocalDate.of(2024, 2, 10), // 설날 (공휴일)
                                LocalDate.of(2024, 2, 12), // 일반일
                                LocalDate.of(2024, 3, 1) // 삼일절 (공휴일)
                );
        }

        @Test
        @DisplayName("[SUCCESS] 공휴일 필터링 성공")
        void testFilterWithHolidays() {
                // given
                when(holidayService.getHolidays("KR", startDate, endDate))
                                .thenReturn(holidays);

                // when
                Set<LocalDate> filteredDates = holidayFilter.filter(dates, startDate, endDate);

                // then
                assertThat(filteredDates).hasSize(2); // 일반일만 남아야 함
                assertThat(filteredDates).containsExactlyInAnyOrder(
                                LocalDate.of(2024, 1, 2),
                                LocalDate.of(2024, 2, 12));
        }

        @Test
        @DisplayName("[SUCCESS] 공휴일이 없는 경우 필터링 성공")
        void testFilterWithoutHolidays() {
                // given
                when(holidayService.getHolidays("KR", startDate, endDate))
                                .thenReturn(Set.of());

                // when
                Set<LocalDate> filteredDates = holidayFilter.filter(dates, startDate, endDate);

                // then
                assertThat(filteredDates).hasSize(6); // 모든 날짜가 남아야 함
                assertThat(filteredDates).containsAll(dates);
        }

        @Test
        @DisplayName("[SUCCESS] 날짜 범위만으로 필터링 성공")
        void testFilterWithDatesOnly() {
                // given
                when(holidayService.getHolidays("KR", dates.stream().min(LocalDate::compareTo).get(),
                                dates.stream().max(LocalDate::compareTo).get()))
                                .thenReturn(holidays);

                // when
                Set<LocalDate> filteredDates = holidayFilter.filter(dates);

                // then
                assertThat(filteredDates).hasSize(2); // 일반일만 남아야 함
                assertThat(filteredDates).containsExactlyInAnyOrder(
                                LocalDate.of(2024, 1, 2),
                                LocalDate.of(2024, 2, 12));
        }

        @Test
        @DisplayName("[FAIL] 빈 날짜 세트로 필터링 시도 시 예외 발생")
        void testFilterWithEmptyDates() {
                // given
                Set<LocalDate> emptyDates = Set.of();

                // when & then
                assertThatThrownBy(() -> holidayFilter.filter(emptyDates))
                                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[SUCCESS] 날짜 범위가 공휴일과 겹치지 않는 경우 필터링 성공")
        void testFilterWithNonOverlappingDates() {
                // given
                Set<LocalDate> nonOverlappingDates = Set.of(
                                LocalDate.of(2024, 4, 1),
                                LocalDate.of(2024, 4, 2),
                                LocalDate.of(2024, 4, 3));
                when(holidayService.getHolidays("KR", nonOverlappingDates.stream().min(LocalDate::compareTo).get(),
                                nonOverlappingDates.stream().max(LocalDate::compareTo).get()))
                                .thenReturn(Set.of());

                // when
                Set<LocalDate> filteredDates = holidayFilter.filter(nonOverlappingDates);

                // then
                assertThat(filteredDates).hasSize(3); // 모든 날짜가 남아야 함
                assertThat(filteredDates).containsAll(nonOverlappingDates);
        }
}