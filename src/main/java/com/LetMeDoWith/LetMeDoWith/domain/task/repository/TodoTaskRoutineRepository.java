package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;

public interface TodoTaskRoutineRepository {

    TodoTaskRoutine save(TodoTaskRoutine todoTaskRoutine);

    void delete(TodoTaskRoutine todoTaskRoutine);
}
