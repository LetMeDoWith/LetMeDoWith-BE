package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

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
