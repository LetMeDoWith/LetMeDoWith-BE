package com.LetMeDoWith.LetMeDoWith.domain.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TodoTaskRepository {

    Optional<TodoTask> getTodoTask(Long id, String memberId);

    List<TodoTask> getTodoTasks(String memberId, LocalDate date);

    List<TodoTask> getTodoTasks(String memberId, Set<LocalDate> dates);

    List<TodoTask> getTodoTasks(TodoTaskRoutine todoTaskRoutine);

    TodoTask saveTodoTask(TodoTask todoTask);

    List<TodoTask> saveTodoTasks(List<TodoTask> todoTasks);

    void deleteTodoTask(TodoTask todoTask);

    void deleteTodoTasks(List<TodoTask> todoTasks);
}
