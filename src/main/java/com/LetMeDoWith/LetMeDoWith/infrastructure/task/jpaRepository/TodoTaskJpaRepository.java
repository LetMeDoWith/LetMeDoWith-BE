package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoTaskJpaRepository extends JpaRepository<TodoTask, Long> {
    
    Optional<TodoTask> findByIdAndMemberId(Long id, Long memberId);
    
    List<TodoTask> findAllByIdAndDate(Long memberId, LocalDate date);
    
    List<TodoTask> findAllByIdAndDateIn(Long memberId, Set<LocalDate> dates);
}