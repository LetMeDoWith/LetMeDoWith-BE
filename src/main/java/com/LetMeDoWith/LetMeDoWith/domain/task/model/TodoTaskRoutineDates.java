package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoTaskRoutineDates {

    private Set<LocalDate> dates;

    public static TodoTaskRoutineDates from(Set<LocalDate> dates) {
        LocalDate minDate =
                dates.stream()
                        .min(LocalDate::compareTo)
                        .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        LocalDate maxDate =
                dates.stream()
                        .max(LocalDate::compareTo)
                        .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));

        if (maxDate.isAfter(minDate.plusYears(5))) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        return new TodoTaskRoutineDates(
                dates.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public void removeDate(LocalDate date) {
        dates.remove(date);
    }

    public void removeDates(Set<LocalDate> dates) {
        this.dates.removeAll(dates);
    }
}
