package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.service.TaskSummaryService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Task Reward", description = "Task 보상")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks/reward")
public class TaskRewardController {

    private final TaskSummaryService taskSummaryService;

    @Operation(summary = "출석 체크 보상 지급", description = "Task 출석체크 보상을 지급을 요청합니다.")
    @ApiSuccessResponse(description = "출석 체크 보상 지급 성공 혹은 실패에 대해 응답합니다.")
    @ApiErrorResponses({
        @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
        @ApiErrorResponse(
                status = FailResponseStatus.DOWITH_TASK_ATTENDACE_REWARD_EXCEED,
                description = "출석체크 보상 지급 횟수를 초과했습니다."),
    })
    @PostMapping("/attendance")
    public ResponseEntity rewardAttendance() {
        taskSummaryService.rewardAttendance(AuthUtil.getMemberId());
        return ResponseUtil.createSuccessResponse();
    }
}
