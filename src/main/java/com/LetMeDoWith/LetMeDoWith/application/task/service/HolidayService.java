package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.HolidayRepository;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayService {
    
    private final HolidayRepository holidayRepository;
    
    /**
     * 특정 기간의 공휴일을 조회한다.
     *
     * @param countryCode 국가 코드
     * @param start       시작일
     * @param end         종료일
     * @return 공휴일 목록
     */
    public Set<LocalDate> getHolidays(CountryCode countryCode, LocalDate start, LocalDate end) {
        return holidayRepository.getHolidays(countryCode, start, end)
                                .stream()
                                .map(Holiday::getDate)
                                .collect(Collectors.toSet());
    }
    
    /**
     * 특정 날짜가 공휴일인지 확인한다.
     *
     * @param date 확인할 날짜
     * @return 공휴일 여부
     */
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.isHoliday(date);
    }
}