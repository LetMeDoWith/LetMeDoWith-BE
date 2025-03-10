package com.LetMeDoWith.LetMeDoWith.application.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import java.time.LocalDate;
import java.util.Set;

public interface HolidayRepository {
    
    boolean isHoliday(LocalDate date);
    
    Set<Holiday> getHolidays(String countryCode, LocalDate startDate, LocalDate endDate);
}