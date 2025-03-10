package com.LetMeDoWith.LetMeDoWith.application.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;

public interface TodoTaskRoutineRepository {
    
    TodoTaskRoutine save(TodoTaskRoutine todoTaskRoutine);
}