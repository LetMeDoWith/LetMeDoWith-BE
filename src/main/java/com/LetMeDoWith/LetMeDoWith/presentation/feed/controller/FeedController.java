package com.LetMeDoWith.LetMeDoWith.presentation.feed.controller;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetreiveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.service.FeedService;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.feed.dto.RetrieveFeedbackAvailableDowithTasksResDto;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/tasks/dowith")
    public ResponseEntity<ResponsePageDto<RetrieveFeedbackAvailableDowithTasksResDto>>
    retrieveFeedbackAvailableDowithTasks(@ParameterObject Pageable pageable) {
        RetreiveFeedbackAvailableDowithTasksResult result = feedService.retrieveFeedbackAvailableDowithTasks(
            pageable);

        return ResponseUtil.createSuccessResponse(
            RetrieveFeedbackAvailableDowithTasksResDto.from(result), pageable, null);
    }
}