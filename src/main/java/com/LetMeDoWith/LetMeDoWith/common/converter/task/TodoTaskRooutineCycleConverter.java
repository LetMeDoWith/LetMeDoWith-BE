package com.LetMeDoWith.LetMeDoWith.common.converter.task;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskRoutineCycle;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TodoTaskRooutineCycleConverter extends
                                            AbstractCombinedEnumConverter<TodoTaskRoutineCycle> {
    
    public TodoTaskRooutineCycleConverter() {
        super(TodoTaskRoutineCycle.class);
    }
    
}