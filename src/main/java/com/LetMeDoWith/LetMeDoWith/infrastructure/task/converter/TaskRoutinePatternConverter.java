package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskRoutinePattern;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class TaskRoutinePatternConverter implements AttributeConverter<TaskRoutinePattern, String> {

    @Override
    public String convertToDatabaseColumn(TaskRoutinePattern pattern) {
        return pattern.getPattern().stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    @Override
    public TaskRoutinePattern convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return TaskRoutinePattern.from(Set.of());
        } else {
            return TaskRoutinePattern.from(
                    Arrays.stream(dbData.split(",")).map(Integer::valueOf).collect(Collectors.toSet()));
        }
    }
}
