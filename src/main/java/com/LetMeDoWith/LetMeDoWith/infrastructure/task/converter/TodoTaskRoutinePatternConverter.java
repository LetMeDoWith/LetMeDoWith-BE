package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutinePattern;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class TodoTaskRoutinePatternConverter implements AttributeConverter<TodoTaskRoutinePattern, String> {

    @Override
    public String convertToDatabaseColumn(TodoTaskRoutinePattern pattern) {
        return pattern.getPattern().stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    @Override
    public TodoTaskRoutinePattern convertToEntityAttribute(String dbData) {
        if (dbData.isBlank()) {
            return TodoTaskRoutinePattern.from(Set.of());
        } else {
            return TodoTaskRoutinePattern.from(
                    Arrays.stream(dbData.split(",")).map(Integer::valueOf).collect(Collectors.toSet()));
        }
    }
}
