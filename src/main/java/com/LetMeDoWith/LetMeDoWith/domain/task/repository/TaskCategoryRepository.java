package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TaskCategory.TaskCategoryCreationType;
import java.util.List;
import java.util.Optional;

public interface TaskCategoryRepository {

    TaskCategory save(TaskCategory taskCategory);

    Optional<TaskCategory> getTaskCategory(Long id, Yn isActive);

    Optional<TaskCategory> getActiveTaskCategory(Long id, String holderId);

    List<TaskCategory> getAllTaskCategories(Yn isActive);

    List<TaskCategory> getCategories(String memberId, Yn isActive);

    List<TaskCategory> getCategories(TaskCategoryCreationType creationType, Yn isActive);
}
