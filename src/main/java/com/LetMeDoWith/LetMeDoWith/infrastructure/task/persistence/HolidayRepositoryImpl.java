package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.HolidayRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository.HolidayJpaRepository;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class HolidayRepositoryImpl implements HolidayRepository {

    private final HolidayJpaRepository holidayJpaRepository;

    @Override
    public boolean isHoliday(LocalDate date) {
        return holidayJpaRepository.existsByDate(date);
    }

    @Override
    public Set<Holiday> getHolidays(CountryCode countryCode, LocalDate startDate, LocalDate endDate) {
        return holidayJpaRepository.findAllByCountryCodeAndDateBetween(countryCode, startDate, endDate);
    }
}
