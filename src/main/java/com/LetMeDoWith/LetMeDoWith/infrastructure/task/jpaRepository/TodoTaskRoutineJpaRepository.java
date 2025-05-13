package com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoTaskRoutineJpaRepository extends JpaRepository<TodoTaskRoutine, Long> {}
