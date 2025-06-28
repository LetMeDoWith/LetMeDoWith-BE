package com.LetMeDoWith.LetMeDoWith.presentation.feedback.controller;

import com.LetMeDoWith.LetMeDoWith.application.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.service.RetrieveTaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.application.service.TaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.CreateDowithFeedbackReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
            req.senderId(), memberId, req.dowithTaskId(), req.taskFeedbackTemplateId());
        
        return ResponseUtil.createSuccessResponse();
    }
    
    @GetMapping("/{dowithTaskId}")
    public ResponseEntity<ResponseDto<RetrieveTaskFeedbackResult>> retrieveTaskFeedbacks(
        @PathVariable Long dowithTaskId) {
        
        return ResponseUtil.createSuccessResponse(
            retrieveTaskFeedbackService.retrieveTaskFeedbacks(dowithTaskId, "KR"));
    }
    
    
}