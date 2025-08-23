package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import jakarta.persistence.Embeddable;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskRoutinePattern {

    private Set<Integer> pattern;

    public static TaskRoutinePattern from(Set<Integer> pattern) {
        return new TaskRoutinePattern(pattern);
    }
}
