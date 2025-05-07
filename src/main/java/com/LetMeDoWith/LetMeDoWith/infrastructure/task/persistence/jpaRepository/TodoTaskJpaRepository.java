package com.LetMeDoWith.LetMeDoWith.infrastructure.task.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoTaskJpaRepository extends JpaRepository<TodoTask, Long>, QTodoTaskRepository {

}