package com.LetMeDoWith.LetMeDoWith.application.task.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import java.util.List;
import java.util.Optional;

public interface TaskCategoryRepository {
    
    TaskCategory save(TaskCategory taskCategory);
    
    Optional<TaskCategory> getTaskCategory(Long id, Yn isActive);
    
    List<TaskCategory> getAllTaskCategories(Yn isActive);
    
    List<TaskCategory> getCategories(Long memberId, Yn isActive);
    
    List<TaskCategory> getCategories(TaskCategoryCreationType creationType, Yn isActive);
    
}