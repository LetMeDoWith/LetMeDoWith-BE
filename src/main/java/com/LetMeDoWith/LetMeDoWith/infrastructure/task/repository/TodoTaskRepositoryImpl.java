package com.LetMeDoWith.LetMeDoWith.infrastructure.task.repository;

import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.task.jpaRepository.TodoTaskJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoTaskRepositoryImpl implements TodoTaskRepository {

    private final TodoTaskJpaRepository todoTaskJpaRepository;

    @Override
    public Optional<TodoTask> getTodoTask(Long id, String memberId) {
        return todoTaskJpaRepository.findTodoTaskAggregate(id, memberId);
    }

    @Override
    public List<TodoTask> getTodoTasks(String memberId, LocalDate date) {
        return todoTaskJpaRepository.findAllTodoTaskAggregates(memberId, date);
    }

    @Override
    public List<TodoTask> getTodoTasks(String memberId, Set<LocalDate> dates) {
        return todoTaskJpaRepository.findAllTodoTaskAggregates(memberId, dates);
    }

    @Override
    public List<TodoTask> getTodoTasks(TodoTaskRoutine todoTaskRoutine) {
        return todoTaskJpaRepository.findAllTodoTaskAggregates(todoTaskRoutine);
    }

    @Override
    public TodoTask saveTodoTask(TodoTask todoTask) {
        return todoTaskJpaRepository.save(todoTask);
    }

    @Override
    public List<TodoTask> saveTodoTasks(List<TodoTask> todoTasks) {
        return todoTaskJpaRepository.saveAll(todoTasks);
    }
}
