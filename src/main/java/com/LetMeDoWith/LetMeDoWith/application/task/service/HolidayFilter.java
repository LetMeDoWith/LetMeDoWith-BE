package com.LetMeDoWith.LetMeDoWith.application.task.service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayFilter {
    
    private final HolidayService holidayService;
    
    public Set<LocalDate> filter(Set<LocalDate> dates, LocalDate startDate, LocalDate endDate) {
        Set<LocalDate> holidays = holidayService.getHolidays("KR", startDate, endDate);
        return dates.stream()
                    .filter(date -> !holidays.contains(date))
                    .collect(Collectors.toSet());
    }
    
    public Set<LocalDate> filter(Set<LocalDate> dates) {
        LocalDate startDate = dates.stream()
                                   .min(LocalDate::compareTo)
                                   .orElseThrow(IllegalArgumentException::new);
        LocalDate endDate = dates.stream()
                                 .max(LocalDate::compareTo)
                                 .orElseThrow(IllegalArgumentException::new);
        
        return filter(dates, startDate, endDate);
    }
}