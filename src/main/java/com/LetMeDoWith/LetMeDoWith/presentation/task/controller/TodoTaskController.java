package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RegisterTodoTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.service.RegisterTodoTaskService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateTodoTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateTodoTaskResDto;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/tasks/todo")
@RequiredArgsConstructor
public class TodoTaskController {
    
    private final RegisterTodoTaskService registerTodoTaskService;
    
    @Operation(summary = "투두모드 태스크 등록", description = "투두모드 태스크를 등록합니다. 루틴이 설정된 Task인 경우 isRoutine을 true로 세팅하고 rountineDates에 Task의 date 포함한 루틴 일자를 리스트로 넣어줍니다.")
    @ApiSuccessResponse(description = "투두모드 Task 생성 성공. 본 API는 생성 성공 여부만 반환합니다. 이후 데이터는 조회 API에서 확인할 수 있습니다.")
    @ApiErrorResponses({
        @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    @PostMapping("")
    public ResponseEntity<ResponseDto<CreateTodoTaskResDto>> createTodoTask(
        @Valid @RequestBody CreateTodoTaskReqDto request) {
        Long memberId = AuthUtil.getMemberId();
        
        RegisterTodoTaskResult result;
        
        if (request.isRoutine()) {
            result = registerTodoTaskService.createTodoTaskWithRoutine(
                memberId,
                request.toCreateTodoTaskCommand());
        } else {
            result = registerTodoTaskService.createTodoTask(
                memberId,
                request.toCreateTodoTaskCommand());
        }
        
        // 생성은 성공 여부만 반환, 이후 데이터는 조회 API에서 확인
        return ResponseUtil.createSuccessResponse();
    }
}