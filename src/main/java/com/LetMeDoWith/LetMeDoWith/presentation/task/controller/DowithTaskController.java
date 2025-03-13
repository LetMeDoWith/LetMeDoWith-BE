package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.service.DeleteDowithTaskService;
import com.LetMeDoWith.LetMeDoWith.application.task.service.RegisterDowithTaskService;
import com.LetMeDoWith.LetMeDoWith.application.task.service.UpdateDowithTaskService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.DowithTask;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateDowithTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateDowithTaskResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskRoutineReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dowith Task", description = "두윗모드 테스크")
@RestController
@RequestMapping("/api/v1/task/dowith")
@RequiredArgsConstructor
public class DowithTaskController {
    
    private final RegisterDowithTaskService registerDowithTaskService;
    private final UpdateDowithTaskService updateDowithTaskService;
    private final DeleteDowithTaskService deleteDowithTaskService;
    
    @Operation(summary = "두윗모드 테스크 생성", description = "두윗모드 테스크를 생성합니다. 루틴이 설정된 Task인 경우 isRoutine을 true로 세팅하고 rountineDates에 Task의 date 포함한 루틴 일자를 리스트로 넣어줍니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 생성 성공. 루틴인 경우 루틴으로 인해 생성된 두윗모드 Task를 포함하여 N개의 Obejct가 반환됩니다.")
    @ApiErrorResponses({
        @ApiErrorResponse(
            status = FailResponseStatus.INVALID_PARAM_ERROR,
            description = "Request Body의 title이 공백이거나, 40자 초과인경우 / startDateTime이 null인 경우 / isRoutine이 null인 경우"
        ),
        @ApiErrorResponse(
            status = FailResponseStatus.DOWITH_TASK_TASK_CATEGORY_NOT_EXIST,
            description = "두윗모드 Task 카테고리가 존재하지 않는 경우"
        ),
        @ApiErrorResponse(
            status = FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED,
            description = "일일 두윗모드 Task 등록 가능 개수를 초과한 경우, 루틴을 가진 Task인 경우 루틴일자들도 검사합니다."
        ),
        @ApiErrorResponse(
            status = FailResponseStatus.DOWITH_TASK_NOT_AVAIL_DATE,
            description = "등록 두윗모드 Task의 일자(루틴포함)가 과거인 경우"
        ),
        @ApiErrorResponse(
            status = FailResponseStatus.DOWITH_TASK_NOT_AVAIL_START_TIME,
            description = "등록 두윗모드 Task의 일자가 오늘 일자인데, 시작시간이 과거인 경우"
        )
    })
    @PostMapping("")
    public ResponseEntity<ResponseDto<CreateDowithTaskResDto>> createDowithTask(
        @Valid @RequestBody CreateDowithTaskReqDto requestBody) {
        
        Long memberId = AuthUtil.getMemberId();
        
        List<DowithTask> dowithTasks = new ArrayList<>();
        if (requestBody.isRoutine()) {
            dowithTasks.addAll(registerDowithTaskService.registerDowithTaskWithRoutine(memberId,
                                                                                       requestBody.toCreateDowithTaskRoutineCommand()));
        } else {
            dowithTasks.add(registerDowithTaskService.registerDowithTask(memberId,
                                                                         requestBody.toCreateDowithTaskCommand()));
        }
        
        return ResponseUtil.createSuccessResponse(
            CreateDowithTaskResDto.toCreateDowithTaskResDto(dowithTasks));
    }
    
    @Operation(summary = "두윗모드 테스크 수정", description = "두윗모드 테스크를 수정합니다.")
    @PutMapping("/{dowithTaskId}")
    public ResponseEntity updateDowithTask(@PathVariable Long dowithTaskId,
                                           @RequestBody UpdateDowithTaskReqDto requestBody) {
        
        Long memberId = AuthUtil.getMemberId();
        
        if (requestBody.isRoutineCreate()) {
            updateDowithTaskService.updateContentsAndCreateRoutine(memberId,
                                                                   requestBody.toCommand(),
                                                                   new HashSet<>(requestBody.routineDates()));
        } else {
            updateDowithTaskService.updateContents(memberId,
                                                   requestBody.toCommand());
        }
        
        return ResponseUtil.createSuccessResponse();
        
    }
    
    @Operation(summary = "두윗모드 테스크 루틴 수정", description = "두윗모드 테스크의 루틴을 수정합니다.")
    @PutMapping("/routine")
    public ResponseEntity updateDowithTaskRoutine(
        @RequestBody UpdateDowithTaskRoutineReqDto requestBody) {
        
        Long memberId = AuthUtil.getMemberId();
        
        updateDowithTaskService.updateRoutine(memberId,
                                              requestBody.dowithTaskId(),
                                              new HashSet<>(requestBody.routineDates()));
        
        return ResponseUtil.createSuccessResponse();
    }
    
    @Operation(summary = "두윗모드 테스크 삭제", description = "두윗모드 테스크를 삭제합니다.")
    @DeleteMapping("/{dowithTaskId}")
    public ResponseEntity deleteDowithTask(@PathVariable Long dowithTaskId,
                                           @RequestParam(name = "isRoutineInclude", required = false, defaultValue = "false") boolean isRoutineInclude) {
        Long memberId = AuthUtil.getMemberId();
        
        if (isRoutineInclude) {
            deleteDowithTaskService.deleteWithRoutines(memberId, dowithTaskId);
        } else {
            deleteDowithTaskService.delete(memberId, dowithTaskId);
        }
        
        return ResponseUtil.createSuccessResponse();
    }
    
}
