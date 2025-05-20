package com.LetMeDoWith.LetMeDoWith.domain.task.service;


import com.LetMeDoWith.LetMeDoWith.common.annotation.DomainService;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTaskRoutine;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * TodoTask의 루틴을 분리하는 도메인 서비스.
 * TodoTask 루틴의 수정, 삭제시 이번만 적용 / 모두 적용의 여부에 따라 루틴을 분리한다.
 */
@DomainService
public class TodoTaskRoutineSplitter {
    
    /**
     * pivot을 기준으로 루틴을 분리한다.
     * isApplyToAll이 true라면 루틴을 피봇 기준으로 분리한다.
     * isApplyToAll이 false라면 피봇만 루틴에서 분리한다.
     *
     * @param todoTasksInRoutine 루틴에 포함된 TodoTask 목록
     * @param pivot              루틴 분리의 기준이 되는 TodoTask
     * @param routine            루틴
     * @return 루틴 분리 결과 (루틴, 새로운 루틴의 TodoTask 목록, 분리된 TodoTask 목록)
     */
    public TodoTaskRoutineSplitResult splitTodoTaskRoutine(List<TodoTask> todoTasksInRoutine,
                                                           TodoTask pivot,
                                                           TodoTaskRoutine routine) {
        
        List<TodoTask> old = new ArrayList<>();
        List<TodoTask> future = new ArrayList<>();
        
        for (TodoTask todoTask : todoTasksInRoutine) {
            if (todoTask.getDate().isBefore(pivot.getDate())) {
                old.add(todoTask);
            } else {
                future.add(todoTask);
            }
        }
        
        // 오래된 것들은 루틴에서 분리
        old.forEach(TodoTask::detachRoutine);
        
        return TodoTaskRoutineSplitResult.of(routine, future, old);
    }
    
    @AllArgsConstructor
    @Getter
    public static class TodoTaskRoutineSplitResult {
        
        private TodoTaskRoutine newRoutine;
        private List<TodoTask> futureTodoTasks;
        private List<TodoTask> detachedTodoTasks;
        
        public static TodoTaskRoutineSplitResult of(TodoTaskRoutine newRoutine,
                                                    List<TodoTask> futureTodoTasks,
                                                    List<TodoTask> detachedTodoTasks) {
            return new TodoTaskRoutineSplitResult(newRoutine,
                                                  futureTodoTasks,
                                                  detachedTodoTasks);
        }
        
        public static TodoTaskRoutineSplitResult of(TodoTaskRoutine newRoutine,
                                                    List<TodoTask> futureTodoTasks,
                                                    TodoTask detachedTodoTask) {
            return new TodoTaskRoutineSplitResult(newRoutine,
                                                  futureTodoTasks,
                                                  List.of(detachedTodoTask));
        }
    }
}