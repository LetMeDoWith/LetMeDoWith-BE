package com.LetMeDoWith.LetMeDoWith.domain.task.service;

import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** TodoTask의 루틴을 분리하는 도메인 서비스. TodoTask 루틴의 수정, 삭제시 이번만 적용 / 모두 적용의 여부에 따라 루틴을 분리한다. */
@DomainService
public class TodoTaskRoutineSplitter {

    /**
     * pivot을 기준으로 루틴을 분할한다. pivot을 포함하여 미래의 TodoTask는 새로운 루틴에 포함되고, 이전의 TodoTask는 기존 루틴에서 분리된다.
     *
     * @param todoTasksInRoutine 루틴에 포함된 TodoTask 목록
     * @param pivot 루틴 분리의 기준이 되는 TodoTask
     * @param routine 루틴
     * @return 루틴 분리 결과 (루틴, 새로운 루틴의 TodoTask 목록, 분리된 TodoTask 목록)
     */
    public TodoTaskRoutineSplitResult splitTodoTaskRoutine(
            List<TodoTask> todoTasksInRoutine, TodoTask pivot, TodoTaskRoutine routine) {

        List<TodoTask> todoTasksToBeDetached = new ArrayList<>();
        List<TodoTask> todoTasksInNewRoutine = new ArrayList<>();

        for (TodoTask todoTask : todoTasksInRoutine) {
            if (todoTask.getDate().isBefore(pivot.getDate())) {
                todoTasksToBeDetached.add(todoTask);
            } else {
                todoTasksInNewRoutine.add(todoTask);
            }
        }

        // 오래된 것들은 루틴에서 분리
        todoTasksToBeDetached.forEach(TodoTask::detachRoutine);

        return TodoTaskRoutineSplitResult.of(routine, todoTasksInNewRoutine, todoTasksToBeDetached);
    }

    @AllArgsConstructor
    @Getter
    public static class TodoTaskRoutineSplitResult {

        private TodoTaskRoutine newRoutine;
        private List<TodoTask> newTodoTasks;
        private List<TodoTask> detachedTodoTasks;

        public static TodoTaskRoutineSplitResult of(
                TodoTaskRoutine newRoutine, List<TodoTask> newTodoTasks, List<TodoTask> detachedTodoTasks) {
            return new TodoTaskRoutineSplitResult(newRoutine, newTodoTasks, detachedTodoTasks);
        }

        public static TodoTaskRoutineSplitResult of(
                TodoTaskRoutine newRoutine, List<TodoTask> newTodoTasks, TodoTask detachedTodoTask) {
            return new TodoTaskRoutineSplitResult(newRoutine, newTodoTasks, List.of(detachedTodoTask));
        }
    }
}
