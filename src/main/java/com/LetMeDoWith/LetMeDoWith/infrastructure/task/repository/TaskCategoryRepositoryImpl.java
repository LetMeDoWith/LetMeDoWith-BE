package com.LetMeDoWith.LetMeDoWith.infrastructure.task.repository;

import com.LetMeDoWith.LetMeDoWith.application.task.repository.TaskCategoryRepository;
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
    public Optional<TaskCategory> getTaskCategory(Long id) {
        return taskCategoryJpaRepository.findById(id);
    }
    
    @Override
    public List<TaskCategory> getAllTaskCategories() {
        return taskCategoryJpaRepository.findAll();
    }
    
    @Override
    public List<TaskCategory> getCategories(Long holderId) {
        return taskCategoryJpaRepository
            .findByCategoryHolderIdAndCreationType(holderId,
                                                   TaskCategoryCreationType.USER_CUSTOM);
    }
    
    @Override
    public List<TaskCategory> getCategories(TaskCategoryCreationType creationType) {
        return taskCategoryJpaRepository.findByCreationType(creationType);
    }
}