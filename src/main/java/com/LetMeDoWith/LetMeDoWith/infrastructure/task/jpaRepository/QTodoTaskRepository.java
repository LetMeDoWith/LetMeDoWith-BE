package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QTodoTaskRepository {
    
    Optional<TodoTask> findTodoTaskAggregate(Long id);
    
    Optional<TodoTask> findTodoTaskAggregate(Long id, Long memberId);
    
    List<TodoTask> findAllTodoTaskAggregates(Long memberId, LocalDate date);
    
    List<TodoTask> findAllTodoTaskAggregates(Long memberId, Set<LocalDate> dates);
    
    List<TodoTask> findAllTodoTaskAggregates(TodoTaskRoutine todoTaskRoutine);
}