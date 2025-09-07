package com.LetMeDoWith.LetMeDoWith.application.task.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.INVALID_REQUEST;

import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRoutineRepository;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineSplitter;
import com.LetMeDoWith.LetMeDoWith.domain.task.service.TodoTaskRoutineSplitter.TodoTaskRoutineSplitResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteTodoTaskService {

    private final TodoTaskRepository todoTaskRepository;
    private final TodoTaskRoutineRepository todoTaskRoutineRepository;
    private final TodoTaskRoutineSplitter splitter;

    /**
     * 투두모드 태스크를 삭제합니다.
     *
     * @param memberId 투두모드 태스크를 삭제할 사용자의 ID
     * @param todoTaskId 삭제할 투두모드 태스크의 ID
     */
    @Transactional
    public void deleteTodoTask(String memberId, Long todoTaskId) {
        TodoTask todoTask = todoTaskRepository
                .getTodoTask(todoTaskId, memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        todoTaskRepository.deleteTodoTask(todoTask);
    }

    /**
     * 루틴 TodoTask를 삭제합니다. 루틴에 속한 모든 TodoTask가 삭제됩니다.
     *
     * @param memberId 루틴 TodoTask를 삭제할 사용자의 ID
     * @param todoTaskId 삭제할 루틴 TodoTask의 ID
     */
    @Transactional
    public void deleteTodoTasksWithRoutine(String memberId, Long todoTaskId) {
        TodoTask todoTask = todoTaskRepository
                .getTodoTask(todoTaskId, memberId)
                .orElseThrow(() -> new RestApiException(INVALID_REQUEST));

        if (!todoTask.isRoutine()) {
            throw new RestApiException(INVALID_REQUEST);
        }

        List<TodoTask> todoTasksInRoutine = todoTaskRepository.getTodoTasks(todoTask.getRoutine());
        TodoTaskRoutineSplitResult splitResult =
                splitter.splitTodoTaskRoutine(todoTasksInRoutine, todoTask, todoTask.getRoutine());

        todoTaskRoutineRepository.delete(splitResult.getNewRoutine());
        todoTaskRepository.deleteTodoTasks(splitResult.getNewTodoTasks());
    }
}
