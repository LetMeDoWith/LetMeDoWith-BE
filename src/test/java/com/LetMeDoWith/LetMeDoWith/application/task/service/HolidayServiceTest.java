package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.LetMeDoWith.LetMeDoWith.application.task.repository.HolidayRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
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
class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private HolidayService holidayService;

    private LocalDate startDate;
    private LocalDate endDate;
    private Set<Holiday> holidays;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
        holidays = Set.of(
                Holiday.of("KR", LocalDate.of(2024, 1, 1), "신정"),
                Holiday.of("KR", LocalDate.of(2024, 2, 9), "설날"),
                Holiday.of("KR", LocalDate.of(2024, 2, 10), "설날"),
                Holiday.of("KR", LocalDate.of(2024, 2, 11), "설날"),
                Holiday.of("KR", LocalDate.of(2024, 3, 1), "삼일절"));
    }

    @Test
    @DisplayName("[SUCCESS] 공휴일 확인 성공")
    void testIsHoliday() {
        // given
        LocalDate holiday = LocalDate.of(2024, 1, 1); // 신정
        when(holidayRepository.isHoliday(holiday)).thenReturn(true);

        // when
        boolean result = holidayService.isHoliday(holiday);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS] 공휴일이 아닌 날짜 확인 성공")
    void testIsHolidayWithNonHoliday() {
        // given
        LocalDate normalDay = LocalDate.of(2024, 1, 2);
        when(holidayRepository.isHoliday(normalDay)).thenReturn(false);

        // when
        boolean result = holidayService.isHoliday(normalDay);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("[SUCCESS] 공휴일 목록 조회 성공")
    void testGetHolidays() {
        // given
        when(holidayRepository.getHolidays("KR", startDate, endDate))
                .thenReturn(holidays);

        // when
        Set<LocalDate> result = holidayService.getHolidays("KR", startDate, endDate);

        // then
        assertThat(result).hasSize(holidays.size());
        assertThat(result).containsAll(holidays.stream()
                .map(Holiday::getDate)
                .toList());
    }

    @Test
    @DisplayName("[SUCCESS] 여러 해에 걸친 공휴일 목록 조회 성공")
    void testGetHolidaysAcrossYears() {
        // given
        LocalDate multiYearStartDate = LocalDate.of(2023, 12, 1);
        LocalDate multiYearEndDate = LocalDate.of(2024, 1, 31);
        Set<Holiday> multiYearHolidays = Set.of(
                Holiday.of("KR", LocalDate.of(2023, 12, 25), "크리스마스"),
                Holiday.of("KR", LocalDate.of(2024, 1, 1), "신정"));
        when(holidayRepository.getHolidays("KR", multiYearStartDate, multiYearEndDate))
                .thenReturn(multiYearHolidays);

        // when
        Set<LocalDate> result = holidayService.getHolidays("KR", multiYearStartDate, multiYearEndDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(
                LocalDate.of(2023, 12, 25),
                LocalDate.of(2024, 1, 1));
    }
}