package com.LetMeDoWith.LetMeDoWith.presentation.feed.controller;

import com.LetMeDoWith.LetMeDoWith.application.feed.dto.RetrieveFeedbackAvailableDowithTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.feed.service.FeedService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.feed.dto.RetrieveFeedbackAvailableDowithTasksResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Feed", description = "둘러보기")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final FeedService feedService;

    @Operation(summary = "잔소리 대상 두윗 태스크 조회", description = "잔소리 대상 두윗 목록을 조회합니다.")
    @ApiSuccessResponse(description = "잔소리 대상 두윗 목록 조회 성공")
    @GetMapping("/tasks/dowith")
    public ResponseEntity<ResponseDto<RetrieveFeedbackAvailableDowithTasksResDto>>
            retrieveFeedbackAvailableDowithTasks() {
        RetrieveFeedbackAvailableDowithTasksResult result = feedService.retrieveFeedbackAvailableDowithTasks();

        return ResponseUtil.createSuccessResponse(RetrieveFeedbackAvailableDowithTasksResDto.from(result));
    }
}
