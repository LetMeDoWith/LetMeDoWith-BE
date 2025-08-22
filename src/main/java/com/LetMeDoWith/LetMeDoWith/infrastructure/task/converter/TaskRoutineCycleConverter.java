package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskRoutineCycleConverter extends AbstractCombinedEnumConverter<TaskRoutineCycle> {

    public TaskRoutineCycleConverter() {
        super(TaskRoutineCycle.class);
    }
}
