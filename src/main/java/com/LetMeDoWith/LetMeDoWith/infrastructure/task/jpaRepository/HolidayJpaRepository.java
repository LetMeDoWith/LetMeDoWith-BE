package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.Holiday;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayJpaRepository extends JpaRepository<Holiday, Long> {
    
    
    boolean existsByDate(LocalDate date);
    
    Set<Holiday> findAllByCountryCodeAndDateBetween(String countryCode,
                                                    LocalDate startDate,
                                                    LocalDate endDate);
}