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
    private Set<Holiday> irregularHolidays;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
        irregularHolidays = Set.of(
                Holiday.of("KR", LocalDate.of(2024, 2, 9), "설날"),
                Holiday.of("KR", LocalDate.of(2024, 2, 10), "설날"),
                Holiday.of("KR", LocalDate.of(2024, 2, 11), "설날"));
    }

    @Test
    @DisplayName("[SUCCESS] 고정 공휴일 확인 성공")
    void testIsHolidayWithFixedHoliday() {
        // given
        LocalDate samiljeol = LocalDate.of(2024, 3, 1); // 삼일절

        // when
        boolean result = holidayService.isHoliday(samiljeol);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS] 비정기 공휴일 확인 성공")
    void testIsHolidayWithIrregularHoliday() {
        // given
        LocalDate seolnal = LocalDate.of(2024, 2, 9); // 설날
        when(holidayRepository.isHoliday(seolnal)).thenReturn(true);

        // when
        boolean result = holidayService.isHoliday(seolnal);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("[SUCCESS] 공휴일이 아닌 날짜 확인 성공")
    void testIsHolidayWithNonHoliday() {
        // given
        LocalDate normalDay = LocalDate.of(2024, 3, 2); // 삼일절 다음날
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
                .thenReturn(irregularHolidays);

        // when
        Set<LocalDate> holidays = holidayService.getHolidays("KR", startDate, endDate);

        // then
        assertThat(holidays).hasSize(irregularHolidays.size() + 7); // 비정기 공휴일 + 고정 공휴일
        assertThat(holidays).containsAll(irregularHolidays.stream()
                .map(Holiday::getDate)
                .toList());
        assertThat(holidays).contains(LocalDate.of(2024, 1, 1)); // 신정
        assertThat(holidays).contains(LocalDate.of(2024, 3, 1)); // 삼일절
        assertThat(holidays).contains(LocalDate.of(2024, 5, 5)); // 어린이날
        assertThat(holidays).contains(LocalDate.of(2024, 6, 6)); // 현충일
        assertThat(holidays).contains(LocalDate.of(2024, 8, 15)); // 광복절
        assertThat(holidays).contains(LocalDate.of(2024, 10, 3)); // 개천절
        assertThat(holidays).contains(LocalDate.of(2024, 10, 9)); // 한글날
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
        Set<LocalDate> holidays = holidayService.getHolidays("KR", multiYearStartDate, multiYearEndDate);

        // then
        assertThat(holidays).hasSize(2); // 크리스마스 + 신정(2024)
        assertThat(holidays).containsExactlyInAnyOrder(
                LocalDate.of(2023, 12, 25), // 크리스마스
                LocalDate.of(2024, 1, 1) // 신정(2024)
        );
    }
}