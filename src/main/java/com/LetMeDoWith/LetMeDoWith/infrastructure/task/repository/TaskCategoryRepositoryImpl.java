package com.LetMeDoWith.LetMeDoWith.infrastructure.task.repository;

import com.LetMeDoWith.LetMeDoWith.application.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.TaskCategoryJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskCategoryRepositoryImpl implements TaskCategoryRepository {
    
    private final TaskCategoryJpaRepository taskCategoryJpaRepository;
    
    @Override
    public TaskCategory save(TaskCategory taskCategory) {
        return taskCategoryJpaRepository.save(taskCategory);
    }
    
    @Override
    public Optional<TaskCategory> getTaskCategory(Long id, Yn isActive) {
        return taskCategoryJpaRepository.findByIdAndIsActive(id, isActive);
    }
    
    @Override
    public List<TaskCategory> getAllTaskCategories(Yn isActive) {
        return taskCategoryJpaRepository.findAllByIsActive(isActive);
    }
    
    @Override
    public List<TaskCategory> getCategories(Long holderId, Yn isActive) {
        return taskCategoryJpaRepository
            .findAllByCategoryHolderIdAndCreationTypeAndIsActive(holderId,
                                                                 TaskCategoryCreationType.USER_CUSTOM,
                                                                 isActive);
    }
    
    @Override
    public List<TaskCategory> getCategories(TaskCategoryCreationType creationType, Yn isActive) {
        return taskCategoryJpaRepository.findAllByCreationTypeAndIsActive(creationType, isActive);
    }
}