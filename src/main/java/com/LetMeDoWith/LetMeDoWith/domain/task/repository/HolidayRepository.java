package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import java.time.LocalDate;
import java.util.Set;

public interface HolidayRepository {

    boolean isHoliday(LocalDate date);

    Set<Holiday> getHolidays(CountryCode countryCode, LocalDate startDate, LocalDate endDate);
}
