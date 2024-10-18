package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoryJpaRepository extends JpaRepository<TaskCategory, Long> {
    
    List<TaskCategory> findByCategoryHolderIdAndCreationType(Long holderId,
                                                             TaskCategoryCreationType creationType);
    
    List<TaskCategory> findByCreationType(TaskCategoryCreationType type);
}