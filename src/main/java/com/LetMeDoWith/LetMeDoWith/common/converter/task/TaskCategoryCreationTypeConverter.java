package com.LetMeDoWith.LetMeDoWith.common.converter.task;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class TaskCategoryCreationTypeConverter
    extends AbstractCombinedConverter<TaskCategoryCreationType> {
    
    public TaskCategoryCreationTypeConverter() {
        super(TaskCategoryCreationType.class);
    }
}