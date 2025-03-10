package com.LetMeDoWith.LetMeDoWith.infrastructure.task.repository;

import com.LetMeDoWith.LetMeDoWith.application.task.repository.HolidayRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.HolidayJpaRepository;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepository {
    
    private final HolidayJpaRepository holidayJpaRepository;
    
    @Override
    public boolean isHoliday(LocalDate date) {
        return holidayJpaRepository.existsByDate(date);
    }
    
    @Override
    public Set<Holiday> getHolidays(String countryCode, LocalDate startDate, LocalDate endDate) {
        return holidayJpaRepository.findAllByCountryCodeAndDateBetween(countryCode,
                                                                       startDate,
                                                                       endDate);
    }
}