package com.LetMeDoWith.LetMeDoWith.infrastructure.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.TodoTaskRoutineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoTaskRoutineRepositoryImpl implements TodoTaskRoutineRepository {

    private final TodoTaskRoutineJpaRepository jpaRepository;

    @Override
    public TodoTaskRoutine save(TodoTaskRoutine todoTaskRoutine) {
        return jpaRepository.save(todoTaskRoutine);
    }
}
