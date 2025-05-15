package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.service.CreateDowithTaskService;
import com.LetMeDoWith.LetMeDoWith.application.task.service.DeleteDowithTaskService;
import com.LetMeDoWith.LetMeDoWith.application.task.service.UpdateDowithTaskService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateDowithTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateDowithTaskRoutineReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashSet;
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
@RequestMapping("/api/v1/tasks/dowith")
@RequiredArgsConstructor
public class DowithTaskController {

    private final CreateDowithTaskService createDowithTaskService;
    private final UpdateDowithTaskService updateDowithTaskService;
    private final DeleteDowithTaskService deleteDowithTaskService;

    @Operation(
            summary = "두윗모드 Task 생성",
            description =
                    "두윗모드 테스크를 생성합니다. 루틴이 설정된 Task인 경우 isRoutine을 true로 세팅하고 rountineDates에 Task의 date 포함한 루틴 일자를 리스트로 넣어줍니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 생성 성공.")
    @ApiErrorResponses({
        @ApiErrorResponse(
                status = FailResponseStatus.INVALID_PARAM_ERROR,
                description =
                        "Request Body의 title이 공백이거나, 40자 초과인경우 / startDateTime이 null인 경우 / isRoutine이 null인 경우"),
        @ApiErrorResponse(
                status = FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED,
                description = "일일 두윗모드 Task 등록 가능 개수를 초과한 경우, 루틴을 가진 Task인 경우 루틴일자들도 검사합니다.")
    })
    @PostMapping("")
    public ResponseEntity createDowithTask(@Valid @RequestBody CreateDowithTaskReqDto requestBody) {

        String memberId = AuthUtil.getMemberId();

        if (requestBody.isRoutine()) {
            createDowithTaskService.createDowithTaskWithRoutine(
                    memberId, requestBody.toCreateDowithTaskRoutineCommand());
        } else {
            createDowithTaskService.createDowithTask(memberId, requestBody.toCreateDowithTaskCommand());
        }

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "두윗모드 Task 수정", description = "두윗모드 Task를 수정합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 수정 성공")
    @ApiErrorResponses({
        @ApiErrorResponse(
                status = FailResponseStatus.INVALID_PARAM_ERROR,
                description =
                        "Request Body의 dowithTaskId null인 경우 / title이 공백이거나, 40자 초과인경우 / isRoutineCreate null인 경우 / isConvertToTodoTask null인 경우"),
        @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우"),
        @ApiErrorResponse(
                status = FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED,
                description = "일일 두윗모드 Task 등록 가능 개수를 초과한 경우")
    })
    @PutMapping("/{dowithTaskId}")
    public ResponseEntity updateDowithTask(
            @PathVariable Long dowithTaskId, @RequestBody UpdateDowithTaskReqDto requestBody) {

        // TODO: Dowith Id Path Variable로 추가, API 상세 로직은 추후 수정 할 것

        String memberId = AuthUtil.getMemberId();

        if (requestBody.isRoutineCreate()) {
            updateDowithTaskService.updateContentsAndCreateRoutine(
                    memberId, requestBody.toCommand(), requestBody.getRoutineDates());
        } else {
            updateDowithTaskService.updateContentsOnly(memberId, requestBody.toCommand());
        }

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "두윗모드 Task 루틴 수정", description = "두윗모드 Task의 루틴을 수정합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 루틴 수정 성공")
    @ApiErrorResponses({
        @ApiErrorResponse(
                status = FailResponseStatus.INVALID_PARAM_ERROR,
                description = "Request Body의 dowithTaskId null인 경우 / routineDates null인 경우"),
        @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우"),
        @ApiErrorResponse(
                status = FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED,
                description = "일일 두윗모드 Task 등록 가능 개수를 초과한 경우")
    })
    @PutMapping("/{dowithTaskId}/routine")
    public ResponseEntity updateDowithTaskRoutine(
            @PathVariable Long dowithTaskId, @RequestBody UpdateDowithTaskRoutineReqDto requestBody) {

        // TODO: Dowith Id Path Variable로 추가, API 상세 로직은 추후 수정 할 것

        String memberId = AuthUtil.getMemberId();

        updateDowithTaskService.updateRoutine(
                memberId, requestBody.dowithTaskId(), new HashSet<>(requestBody.routineDates()));

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "두윗모드 Task 삭제", description = "두윗모드 Task를 삭제합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 삭제 성공")
    @ApiErrorResponses({
        @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")
    })
    @DeleteMapping("/{dowithTaskId}")
    public ResponseEntity deleteDowithTask(
            @PathVariable Long dowithTaskId,
            @RequestParam(name = "isRoutineInclude", required = false, defaultValue = "false")
                    boolean isRoutineInclude) {
        String memberId = AuthUtil.getMemberId();

        if (isRoutineInclude) {
            deleteDowithTaskService.deleteWithRoutines(memberId, dowithTaskId);
        } else {
            deleteDowithTaskService.delete(memberId, dowithTaskId);
        }

        return ResponseUtil.createSuccessResponse();
    }
}
