package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TaskRoutineCycle;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskRooutineCycleConverter extends AbstractCombinedEnumConverter<TaskRoutineCycle> {

    public TaskRooutineCycleConverter() {
        super(TaskRoutineCycle.class);
    }
}
