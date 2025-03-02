package com.LetMeDoWith.LetMeDoWith.application.task.service;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.TodoTaskVO;
import com.LetMeDoWith.LetMeDoWith.application.task.repository.TaskCategoryRepository;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.repository.TodoTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterTodoTaskService {
    
    private final TodoTaskRepository todoTaskRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    
    /**
     * 루틴이 아닌 TodoTask를 생성한다.
     *
     * @param memberId TodoTask를 생성할 사용자의 ID
     * @param command  생성할 TodoTask의 정보
     * @return 생성된 TodoTask
     */
    public RegisterTodoTaskResult registerTodoTask(Long memberId, CreateTodoTaskCommand command) {
        if (command.taskCategoryId() != null) {
            taskCategoryRepository.getActiveTaskCategory(command.taskCategoryId(), memberId)
                                  .orElseThrow(() -> new RestApiException(
                                      FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST));
        }
        
        TodoTask todoTask = TodoTask.of(memberId,
                                        command.taskCategoryId(),
                                        command.title(),
                                        command.date(),
                                        command.startTime());
        
        return RegisterTodoTaskResult.of(TodoTaskVO.from(todoTaskRepository.saveTodoTask(todoTask)));
    }
}