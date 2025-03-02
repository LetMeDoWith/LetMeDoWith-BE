package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.CreateTodoTaskCommand;
import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.service.RegisterTodoTaskService;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateTodoTaskResDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Todo Task", description = "투두모드 태스크")
@RestController
@RequestMapping("/api/v1/task/todo")
@RequiredArgsConstructor
public class TodoTaskController {
    
    private final RegisterTodoTaskService registerTodoTaskService;
    
    @PostMapping("")
    public ResponseEntity<ResponseDto<CreateTodoTaskResDto>> registerTodoTask(
        @Valid @RequestBody CreateTodoTaskCommand request) {
        Long memberId = AuthUtil.getMemberId();
        
        RegisterTodoTaskResult registerTodoTaskResult = registerTodoTaskService.registerTodoTask(
            memberId,
            request);
        
        return ResponseUtil.createSuccessResponse(
            CreateTodoTaskResDto.of(registerTodoTaskResult.todoTaskList(),
                                    registerTodoTaskResult.isRoutine())
        );
    }
}