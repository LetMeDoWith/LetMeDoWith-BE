package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.*;
import com.LetMeDoWith.LetMeDoWith.application.task.service.DeleteTodoTaskService;
import com.LetMeDoWith.LetMeDoWith.application.task.service.RegisterTodoTaskService;
import com.LetMeDoWith.LetMeDoWith.application.task.service.UpdateTodoTaskService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.TodoTask;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.CreateTodoTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateTodoTaskReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateTodoTaskRoutineReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.UpdateTodoTaskWithRoutineReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Todo Task", description = "투두모드 태스크")
@RestController
@RequestMapping("/api/v1/tasks/todo")
@RequiredArgsConstructor
public class TodoTaskController {

    private final RegisterTodoTaskService registerTodoTaskService;
    private final UpdateTodoTaskService updateTodoTaskService;
    private final DeleteTodoTaskService deleteTodoTaskService;

    @Operation(
            summary = "투두모드 태스크 등록",
            description =
                    "투두모드 태스크를 등록합니다. 루틴이 설정된 Task인 경우 isRoutine을 true로 세팅하고 rountineDates에 Task의 date 포함한 루틴 일자를 리스트로 넣어줍니다.")
    @ApiSuccessResponse(description = "투두모드 Task 생성 성공. 본 API는 생성 성공 여부만 반환합니다. 이후 데이터는 조회 API에서 확인할 수 있습니다.")
    @ApiErrorResponses({
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    @PostMapping("")
    public ResponseEntity registerTodoTask(@Valid @RequestBody CreateTodoTaskReqDto request) {
        String memberId = AuthUtil.getMemberId();

        RegisterTodoTaskResult result;

        RegisterTodoTaskCommand command = RegisterTodoTaskCommand.of(
                request.taskCategoryId(),
                request.title(),
                request.date(),
                request.startTime(),
                request.routineCondition());

        if (request.routineCondition() != null) {
            result = registerTodoTaskService.registerTodoTaskWithRoutine(memberId, command);
        } else {
            result = registerTodoTaskService.registerTodoTask(memberId, command);
        }

        // 생성은 성공 여부만 반환, 이후 데이터는 조회 API에서 확인
        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "투두모드 태스크 수정", description = "투두모드 태스크를 1개 수정합니다. 컨텐츠를 수정하거나, 루틴이 아닌 경우 루틴으로 변환할 수 있습니다.")
    @ApiSuccessResponse(description = "투두모드 Task 수정 성공. 본 API는 생성 성공 여부만 반환합니다. 이후 데이터는 조회 API에서 확인할 수 있습니다.")
    @ApiErrorResponses({
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    @PutMapping("/{todoTaskId}")
    public ResponseEntity updateSingleTodoTask(
            @PathVariable Long todoTaskId, @RequestBody UpdateTodoTaskReqDto request) {
        String memberId = AuthUtil.getMemberId();

        TaskRoutineCondition routineCondition = null;

        if (request.routineCondition() != null) {
            routineCondition = TaskRoutineCondition.of(
                    request.routineCondition().startDate(),
                    request.routineCondition().endDate(),
                    request.routineCondition().cycle(),
                    request.routineCondition().pattern(),
                    request.routineCondition().isExcludeHolidays());
        }

        updateTodoTaskService.updateSingleTodoTask(
                memberId,
                todoTaskId,
                UpdateTodoTaskCommand.of(
                        request.title(),
                        request.startDateTime().toLocalTime(),
                        request.taskCategoryId(),
                        routineCondition));

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "투두모드 태스크(루틴포함) 수정", description = "투두모드 루틴의 모든 태스크를 수정합니다.")
    @ApiSuccessResponse(description = "투두모드 태스크 수정 성공. 본 API는 생성 성공 여부만 반환합니다. 이후 데이터는 조회 API에서 확인할 수 있습니다.")
    @ApiErrorResponses({
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    @PutMapping("/{todoTaskId}/with-routine")
    public ResponseEntity updateTodoTaskWithRoutine(
            @PathVariable Long todoTaskId, @RequestBody UpdateTodoTaskWithRoutineReqDto request) {
        String memberId = AuthUtil.getMemberId();

        UpdateTodoTaskWithRoutineCommand command = UpdateTodoTaskWithRoutineCommand.of(
                request.title(), request.startDateTime().toLocalTime(), request.taskCategoryId());

        updateTodoTaskService.updateTodoTaskWithRoutine(memberId, todoTaskId, command);

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "투두모드 태스크 루틴 수정", description = "투두모드 루틴을 수정합니다. 루틴 조건을 수정합니다.")
    @ApiSuccessResponse(description = "투두모드 루틴 수정 성공. 본 API는 생성 성공 여부만 반환합니다. 이후 데이터는 조회 API에서 확인할 수 있습니다.")
    @ApiErrorResponses({
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    @PutMapping("/{todoTaskId}/routine")
    public ResponseEntity updateTodoTaskRoutine(
            @PathVariable Long todoTaskId, @RequestBody UpdateTodoTaskRoutineReqDto request) {
        String memberId = AuthUtil.getMemberId();

        UpdateTodoTaskRoutineCommand command = UpdateTodoTaskRoutineCommand.of(
                request.startDate(),
                request.endDate(),
                request.cycle(),
                request.pattern(),
                request.isExcludeHolidays());

        updateTodoTaskService.updateTodoTaskRoutine(memberId, todoTaskId, command);

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "투두모드 태스크 완료", description = "투두모드 태스크를 완료합니다.")
    @ApiSuccessResponse(description = "투두모드 태스크 완료 성공. 본 API는 완료된 태스크의 ID를 반환합니다.")
    @ApiErrorResponses({
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    @PatchMapping("/{todoTaskId}/complete")
    public ResponseEntity<ResponseDto<Long>> completeTodoTask(@PathVariable Long todoTaskId) {
        String memberId = AuthUtil.getMemberId();
        TodoTask completeTodoTask = updateTodoTaskService.completeTodoTask(memberId, todoTaskId);
        return ResponseUtil.createSuccessResponse(completeTodoTask.getId());
    }

    @Operation(summary = "투두모드 태스크 완료 취소", description = "완료된 투두모드 태스크를 대기 상태로 변경합니다.")
    @ApiSuccessResponse(description = "투두모드 태스크 완료 취소 성공. 본 API는 대기상태로 전환된 태스크의 ID를 반환합니다.")
    @ApiErrorResponses({
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    @PatchMapping("/{todoTaskId}/wait")
    public ResponseEntity<ResponseDto<Long>> waitTodoTask(@PathVariable Long todoTaskId) {
        String memberId = AuthUtil.getMemberId();
        TodoTask waitTodoTask = updateTodoTaskService.waitTodoTask(memberId, todoTaskId);
        return ResponseUtil.createSuccessResponse(waitTodoTask.getId());
    }

    @DeleteMapping("/{todoTaskId}")
    @Operation(summary = "투두모드 태스크 삭제", description = "투두모드 태스크를 삭제합니다.")
    @ApiSuccessResponse(description = "투두모드 태스크 삭제 성공.")
    @ApiErrorResponses({
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    public ResponseEntity deleteTodoTask(@PathVariable Long todoTaskId) {
        String memberId = AuthUtil.getMemberId();

        deleteTodoTaskService.deleteTodoTask(memberId, todoTaskId);
        return ResponseUtil.createSuccessResponse();
    }

    @DeleteMapping("/{todoTaskId}/with-routine")
    @Operation(summary = "투두모드 태스크(루틴포함) 삭제", description = "투두모드 루틴을 포함한 태스크를 삭제합니다. 루틴이 설정된 태스크의 경우 루틴과 함께 삭제됩니다.")
    @ApiSuccessResponse(description = "투두모드 태스크(루틴포함) 삭제 성공.")
    @ApiErrorResponses({
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청입니다."),
    })
    public ResponseEntity deleteTodoTaskWithRoutine(@PathVariable Long todoTaskId) {
        String memberId = AuthUtil.getMemberId();

        deleteTodoTaskService.deleteTodoTasksWithRoutine(memberId, todoTaskId);
        return ResponseUtil.createSuccessResponse();
    }
}
