package com.LetMeDoWith.LetMeDoWith.presentation.feedback.controller;

import com.LetMeDoWith.LetMeDoWith.application.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.service.RetrieveTaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.application.service.TaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.CreateDowithFeedbackReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    
    
    @PostMapping("")
    public ResponseEntity createDowithFeedback(
        @Valid @RequestBody CreateDowithFeedbackReqDto req) {
        String memberId = AuthUtil.getMemberId();
        
        feedbackService.createDowithFeedback(
            memberId, req.dowithTaskId(), req.taskFeedbackTemplateId());
        
        return ResponseUtil.createSuccessResponse();
    }
    
    @GetMapping("/")
    public ResponseEntity<ResponseDto<RetrieveTaskFeedbackResult>> retrieveTaskFeedbacks(
        @RequestParam(value = "taskId", required = false) Long taskId,
        @RequestParam(value = "senderId", required = false) Long senderId,
        @RequestParam(value = "receiverId", required = false) Long receiverId) {
        
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
                retrieveTaskFeedbackService.retrieveTaskFeedbacksBySenderId(senderId.toString(),
                                                                            "KR"));
        } else if (receiverId != null) {
            return ResponseUtil.createSuccessResponse(
                retrieveTaskFeedbackService.retrieveTaskFeedbacksByReceiverId(receiverId.toString(),
                                                                              "KR"));
        } else {
            throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
        }
    }
    
    
}