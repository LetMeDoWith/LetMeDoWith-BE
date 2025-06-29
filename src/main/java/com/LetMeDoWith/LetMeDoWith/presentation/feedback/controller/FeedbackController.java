package com.LetMeDoWith.LetMeDoWith.presentation.feedback.controller;

import com.LetMeDoWith.LetMeDoWith.application.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.service.RetrieveTaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.application.service.TaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.CreateDowithFeedbackReqDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
    
    private final TaskFeedbackService feedbackService;
    private final RetrieveTaskFeedbackService retrieveTaskFeedbackService;
    
    @Operation(summary = "두윗 태스크 잔소리 생성",
        description = "두윗모드 잔소리를 생성합니다.")
    @ApiSuccessResponse(
        description = "두윗모드 잔소리 생성 성공. 본 API는 생성 성공 여부만 반환합니다.")
    @ApiErrorResponses({
        @ApiErrorResponse(
            status = FailResponseStatus.MEMBER_NOT_EXIST,
            description = "존재하지 않는 회원입니다."),
        @ApiErrorResponse(
            status = FailResponseStatus.INVALID_REQUEST,
            description = "잘못된 요청입니다. (예: 잔소리 불가능한 상태에서 생성 요청하는 경우 등)")
    })
    @PostMapping("")
    public ResponseEntity createDowithFeedback(
        @Valid @RequestBody CreateDowithFeedbackReqDto req) {
        String memberId = AuthUtil.getMemberId();
        
        feedbackService.createDowithFeedback(
            memberId, req.dowithTaskId(), req.taskFeedbackTemplateId());
        
        return ResponseUtil.createSuccessResponse();
    }
    
    @Operation(summary = "두윗 태스크 잔소리 조회",
        description = "두윗모드 잔소리를 조회합니다. taskId, senderId, receiverId를 Query Param으로 조회 가능합니다.")
    @ApiSuccessResponse(
        description = "두윗모드 잔소리 조회 성공. 조회된 잔소리 목록을 반환합니다.")
    @ApiErrorResponses({
        @ApiErrorResponse(
            status = FailResponseStatus.INVALID_PARAM_ERROR,
            description = "파라미터를 정확히 1개만 요청하지 않은 경우 (예: taskId, senderId, receiverId 중 하나만 제공해야 함)"),
        @ApiErrorResponse(
            status = FailResponseStatus.MEMBER_NOT_EXIST,
            description = "존재하지 않는 회원입니다.")
    })
    @GetMapping("/")
    public ResponseEntity<ResponseDto<RetrieveTaskFeedbackResult>> retrieveTaskFeedbacks(
        @RequestParam(value = "taskId", required = false) Long taskId,
        @RequestParam(value = "senderId", required = false) String senderId,
        @RequestParam(value = "receiverId", required = false) String receiverId) {
        
        int paramCount = 0;
        
        if (taskId != null) {
            paramCount++;
        }
        if (senderId != null) {
            paramCount++;
        }
        if (receiverId != null) {
            paramCount++;
        }
        
        if (paramCount != 1) {
            throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
        }
        
        if (taskId != null) {
            return ResponseUtil.createSuccessResponse(
                retrieveTaskFeedbackService.retrieveTaskFeedbacksByTaskId(taskId, "KR"));
        } else if (senderId != null) {
            return ResponseUtil.createSuccessResponse(
                retrieveTaskFeedbackService.retrieveTaskFeedbacksBySenderId(senderId, "KR"));
        } else if (receiverId != null) {
            return ResponseUtil.createSuccessResponse(
                retrieveTaskFeedbackService.retrieveTaskFeedbacksByReceiverId(receiverId, "KR"));
        } else {
            throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
        }
    }
    
    @Operation(summary = "두윗 태스크 잔소리 확인",
        description = "두윗모드 잔소리를 확인합니다.")
    @ApiSuccessResponse(
        description = "두윗모드 잔소리 확인 성공. 본 API는 확인 성공 여부만 반환합니다.")
    @PatchMapping("/{feedbackId}/check")
    public ResponseEntity checkDowithTaskFeedback(@RequestParam("feedbackId") Long feedbackId) {
        
        feedbackService.checkDowithFeedbacks(List.of(feedbackId));
        return ResponseUtil.createSuccessResponse();
    }
    
}