package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory;
import com.LetMeDoWith.LetMeDoWith.domain.task.TaskCategory.TaskCategoryCreationType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoryJpaRepository extends JpaRepository<TaskCategory, Long> {
    
    Optional<TaskCategory> findByIdAndIsActive(Long id, Yn isActive);
    
    List<TaskCategory> findAllByIsActive(Yn isActive);
    
    List<TaskCategory> findAllByCategoryHolderIdAndCreationTypeAndIsActive(Long holderId,
                                                                           TaskCategoryCreationType taskCategoryCreationType,
                                                                           Yn isActive);
    
    List<TaskCategory> findAllByCreationTypeAndIsActive(TaskCategoryCreationType creationType,
                                                        Yn isActive);
}