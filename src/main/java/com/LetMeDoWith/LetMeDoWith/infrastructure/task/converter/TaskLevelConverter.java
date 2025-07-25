package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.task.TaskCompleteLevel;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskLevelConverter extends AbstractCombinedEnumConverter<TaskCompleteLevel> {

    public TaskLevelConverter() {
        super(TaskCompleteLevel.class);
    }
}
