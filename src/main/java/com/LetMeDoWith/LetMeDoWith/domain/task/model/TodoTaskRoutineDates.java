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
        dates.forEach(date -> {
            if (date.isBefore(LocalDate.now())) {
                throw new RestApiException(FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE);
            }
        });
        return new TodoTaskRoutineDates(
            dates.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new)));
    }
}