package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.service.RetrieveTaskService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.dto.DowithTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.dto.TodoTaskQueryDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.RetrieveTasksResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
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
    public ResponseEntity<ResponseDto<RetrieveTasksResDto>> retrieveTasks(
        @RequestParam(value = "startDate") LocalDate startDate,
        @RequestParam(value = "endDate") LocalDate endDate) {
        
        if (startDate.isBefore(endDate)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
        
        if (endDate.minusDays(startDate.toEpochDay()).getDayOfYear() > 40) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
        
        Long memberId = AuthUtil.getMemberId();
        
        List<TodoTaskQueryDto> todoTaskQueryDtos = retrieveTaskService.retrieveTodoTasks(
            memberId,
            startDate,
            endDate);
        
        List<DowithTaskQueryDto> dowithTaskQueryDtos = retrieveTaskService.retrieveDowithTasks(
            memberId,
            startDate,
            endDate);
        
        return ResponseUtil.createSuccessResponse(RetrieveTasksResDto.of(
            todoTaskQueryDtos,
            dowithTaskQueryDtos
        ));
        
    }
    
}
