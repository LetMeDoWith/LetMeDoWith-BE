package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.service.TaskRewardService;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
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

    private final TaskRewardService taskRewardService;

    @PostMapping("/attendance")
    public ResponseEntity rewardAttendance() {

        taskRewardService.rewardAttendance(AuthUtil.getMemberId());
        return ResponseUtil.createSuccessResponse();
    }

}
