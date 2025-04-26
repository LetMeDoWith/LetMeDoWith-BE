package com.LetMeDoWith.LetMeDoWith.common.converter.task;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.TodoTaskStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TodoTaskStatusConverter extends AbstractCombinedConverter<TodoTaskStatus> {
    
    public TodoTaskStatusConverter() {
        super(TodoTaskStatus.class);
    }
    
}