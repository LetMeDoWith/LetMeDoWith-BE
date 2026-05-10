package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveTasksResult;
import com.LetMeDoWith.LetMeDoWith.application.task.service.RetrieveTaskService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTasksResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Month;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Task", description = "테스크")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final RetrieveTaskService retrieveTaskService;

    @Operation(summary = "테스크 목록 조회", description = "테스크 목록을 조회합니다.")
    @ApiSuccessResponse(description = "TodoTask 목록과 DowithTask 목록을 Query Parameter 조건에 맞게 반환합니다")
    @ApiErrorResponses({
        @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    @GetMapping("")
    public ResponseEntity<ResponseDto<RetrieveTasksResDto>> retrieveMonthTasks(
            @Parameter(description = "조회 연도", example = "2026") @RequestParam(value = "year") int year,
            @Parameter(description = "조회 월 (1–12)", example = "5") @RequestParam(value = "month") int month) {

        RetrieveTasksResult result = retrieveTaskService.retrieveMonthTasks(Year.of(year), Month.of(month));

        return ResponseUtil.createSuccessResponse(RetrieveTasksResDto.from(result));
    }
}
