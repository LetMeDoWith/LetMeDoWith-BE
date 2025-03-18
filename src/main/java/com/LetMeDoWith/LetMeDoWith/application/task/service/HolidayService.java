package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.repository.HolidayRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.constants.HolidayConstants;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayService {
    
    private final HolidayRepository holidayRepository;
    
    /**
     * 특정 날짜가 공휴일인지 확인한다.
     * 상수 목록에서 우선 확인 후, 아니라면 비정기 공휴일에서 탐색한다.
     *
     * @param date 확인할 날짜
     * @return 공휴일 여부
     */
    public boolean isHoliday(LocalDate date) {
        if (HolidayConstants.FIXED_HOLIDAYS.contains(MonthDay.from(date))) {
            return true;
        }
        
        return holidayRepository.isHoliday(date);
    }
    
    /**
     * 입력받은 범위에 포함되는 공휴일 목록을 조회한다.
     * 고정 공휴일과 비정기 공휴일을 합쳐서 반환한다.
     *
     * @param countryCode 국가 코드
     * @param start       시작 날짜
     * @param end         종료 날짜
     * @return 공휴일 목록
     */
    public Set<LocalDate> getHolidays(String countryCode, LocalDate start, LocalDate end) {
        // DB에서 비정기 공휴일 조회
        Set<LocalDate> irregularHolidays = holidayRepository.getHolidays(
                                                                countryCode,
                                                                start,
                                                                end)
                                                            .stream()
                                                            .map(Holiday::getDate)
                                                            .collect(Collectors.toSet());
        
        // 고정 공휴일 계산
        Set<LocalDate> fixedHolidays = computeFixedHolidays(start, end);
        
        // 두 공휴일 목록을 합쳐서 반환
        fixedHolidays.addAll(irregularHolidays);
        return fixedHolidays;
    }
    
    /**
     * 입력받은 범위에 포함되는 고정 공휴일(삼일절, 광복절 등) 목록을 계산한다.
     *
     * @param start 시작 날짜
     * @param end   종료 날짜
     * @return 고정 공휴일 목록
     */
    private Set<LocalDate> computeFixedHolidays(LocalDate start, LocalDate end) {
        Set<LocalDate> fixedHolidays = new TreeSet<>();
        
        for (int year = start.getYear(); year <= end.getYear(); year++) {
            for (MonthDay md : HolidayConstants.FIXED_HOLIDAYS) {
                LocalDate holiday = md.atYear(year);
                
                if (!holiday.isBefore(start) && !holiday.isAfter(end)) {
                    fixedHolidays.add(holiday);
                }
            }
        }
        return fixedHolidays;
    }
}