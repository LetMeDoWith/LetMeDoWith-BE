package com.LetMeDoWith.LetMeDoWith.application.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import java.util.List;
import java.util.Optional;

public interface TaskCategoryRepository {
    
    TaskCategory save(TaskCategory taskCategory);
    
    Optional<TaskCategory> getTaskCategory(Long id);
    
    List<TaskCategory> getAllTaskCategories();
    
    List<TaskCategory> getCategories(Long memberId);
    
    List<TaskCategory> getCategories(TaskCategoryCreationType creationType);
    
}