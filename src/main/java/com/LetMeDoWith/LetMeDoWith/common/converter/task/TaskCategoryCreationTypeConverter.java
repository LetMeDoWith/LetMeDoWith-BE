package com.LetMeDoWith.LetMeDoWith.common.converter.task;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory.TaskCategoryCreationType;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class TaskCategoryCreationTypeConverter
    extends AbstractCombinedEnumConverter<TaskCategoryCreationType> {
    
    public TaskCategoryCreationTypeConverter() {
        super(TaskCategoryCreationType.class);
    }
}