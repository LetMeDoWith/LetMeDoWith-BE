package com.LetMeDoWith.LetMeDoWith.presentation.task.controller;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveDowithTaskResult;
import com.LetMeDoWith.LetMeDoWith.application.task.service.*;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.task.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dowith Task", description = "두윗모드 테스크")
@RestController
@RequestMapping("/api/v1/tasks/dowith")
@RequiredArgsConstructor
public class DowithTaskController {

    private final CreateDowithTaskService createDowithTaskService;
    private final UpdateDowithTaskService updateDowithTaskService;
    private final DeleteDowithTaskService deleteDowithTaskService;
    private final SuccessDowithTaskService successDowithTaskService;
    private final RetrieveTaskService retrieveTaskService;
    private final TaskSummaryService taskSummaryService;

    @Operation(summary = "두윗모드 Task 조회")
    @ApiSuccessResponse(description = "두윗모드 Task 조회 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")})
    @GetMapping("/{dowithTaskId}")
    public ResponseEntity retrieveDowithTask(@PathVariable Long dowithTaskId) {
        RetrieveDowithTaskResult result = this.retrieveTaskService.retrieveDowithTask(dowithTaskId);
        return ResponseUtil.createSuccessResponse(RetrieveDowithTaskResDto.from(result));
    }

    @Operation(summary = "두윗모드 Task 생성", description = "두윗모드 테스크를 생성합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 생성 성공.")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")})
    @PostMapping("")
    public ResponseEntity createDowithTask(@Valid @RequestBody CreateDowithTaskReqDto requestBody) {

        String memberId = AuthUtil.getMemberId();

        if (requestBody.routineCondition() == null) {
            createDowithTaskService.createDowithTask(requestBody.toCreateDowithTaskCommand());
        } else {
            createDowithTaskService.createDowithTaskWithRoutine(requestBody.toCreateDowithTaskWithRoutineCommand());
        }

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "두윗모드 Task 수정", description = "두윗모드 Task를 수정합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 수정 성공")
    @ApiErrorResponses({
            @ApiErrorResponse(
                    status = FailResponseStatus.INVALID_PARAM_ERROR,
                    description =
                            "Request Body의 title이 공백이거나, 40자 초과인경우 / date, startTime이 null인 경우 / routine의 startDate가 date와 일치하지 않는 경우"),
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우"),
            //        @ApiErrorResponse(
            //                status = FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED,
            //                description = "일일 두윗모드 Task 등록 가능 개수를 초과한 경우")
    })
    @PutMapping("/{dowithTaskId}")
    public ResponseEntity updateDowithTask(
            @PathVariable Long dowithTaskId, @RequestBody UpdateDowithTaskReqDto requestBody) {

        updateDowithTaskService.updateDowithTaskContentsAndCreateRoutine(
                requestBody.toCommand(dowithTaskId));
        return ResponseUtil.createSuccessResponse();

    }

    @Operation(summary = "두윗모드 Task(루틴포함) 수정", description = "두윗모드 루틴의 모든 Task를 수정합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 수정 성공")
    @ApiErrorResponses({
            @ApiErrorResponse(
                    status = FailResponseStatus.INVALID_PARAM_ERROR,
                    description =
                            "Request Body의 title이 공백이거나, 40자 초과인경우 / date, startTime이 null인 경우 / routine의 startDate가 date와 일치하지 않는 경우"),
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우"),
            //        @ApiErrorResponse(
            //                status = FailResponseStatus.DOWITH_TASK_CREATE_COUNT_EXCEED,
            //                description = "일일 두윗모드 Task 등록 가능 개수를 초과한 경우")
    })
    @PutMapping("/{dowithTaskId}/with-routine")
    public ResponseEntity updateDowithTaskWithRoutine(
            @PathVariable Long dowithTaskId, @RequestBody UpdateDowithTaskWithRoutineReqDto requestBody) {

        updateDowithTaskService.updateDowithTaskContentsOnly(requestBody.toCommand(dowithTaskId));
        return ResponseUtil.createSuccessResponse();

    }

    @Operation(summary = "두윗모드 Task 루틴 수정", description = "두윗모드 Task의 루틴을 수정합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 루틴 수정 성공")
    @ApiErrorResponses({
            @ApiErrorResponse(
                    status = FailResponseStatus.INVALID_PARAM_ERROR,
                    description =
                            "Request Body의 title이 공백이거나, 40자 초과인경우 / date, startTime이 null인 경우 / routine의 startDate가 date와 일치하지 않는 경우"),
            @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")
    })
    @PutMapping("/{dowithTaskId}/routine")
    public ResponseEntity updateDowithTaskRoutine(
            @PathVariable Long dowithTaskId, @RequestBody UpdateDowithTaskRoutineReqDto requestBody) {

        updateDowithTaskService.updateRoutine(requestBody.toCommand(dowithTaskId));

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "두윗모드 Task 삭제", description = "두윗모드 Task를 삭제합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 삭제 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")})
    @DeleteMapping("/{dowithTaskId}")
    public ResponseEntity deleteDowithTask(@PathVariable Long dowithTaskId) {

        deleteDowithTaskService.delete(AuthUtil.getMemberId(), dowithTaskId);

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "두윗모드 Task(Routine 포함) 삭제", description = "두윗모드 Task(Routine 포함)를 삭제합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task(Routine 포함) 삭제 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")})
    @DeleteMapping("/{dowithTaskId}/with-routine")
    public ResponseEntity deleteDowithTaskWithRoutine(@PathVariable Long dowithTaskId) {

        deleteDowithTaskService.deleteWithRoutines(AuthUtil.getMemberId(), dowithTaskId);

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "두윗모드 Task 인증 이미지 upload presigned url 발급")
    @ApiSuccessResponse(
            description =
                    "요청 시의 imageFileNames 수 만큼 presigned url이 발급됩니다. method를 참고하여 presigned url 하나당 이미지 하나를 http request 하여 업로드합니다.")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")})
    @PostMapping("/{dowithTaskId}/success/image/upload-presigned-url")
    public ResponseEntity<ResponseDto<GenerateDowithTaskSuccessImageUploadPresignedUrlsResDto>>
    generateDowithTaskSuccessImageUploadPresignedUrls(
            @PathVariable Long dowithTaskId,
            @RequestBody GenerateDowithTaskSuccessImageUploadPresignedUrlsReqDto requestBody) {

        String memberId = AuthUtil.getMemberId();

        List<String> presignedUrls = successDowithTaskService.generateDowithTaskSuccessImageUploadPresignedUrls(
                memberId, dowithTaskId, requestBody.imageFileNames());

        return ResponseUtil.createSuccessResponse(
                new GenerateDowithTaskSuccessImageUploadPresignedUrlsResDto(presignedUrls, "POST"));
    }

    @Operation(summary = "두윗모드 Task 인증", description = "Presigned url을 통해서 업로드한 파일의 public url을 body에 담아 요청합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 인증 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")})
    @PostMapping("/{dowithTaskId}/success")
    public ResponseEntity<ResponseDto<Object>> successDowithTask(
            @PathVariable Long dowithTaskId, @RequestBody successDowithTaskReqDto requestBody) {

        String memberId = AuthUtil.getMemberId();

        successDowithTaskService.successDowithTask(memberId, dowithTaskId, requestBody.publicImageUrls());

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "두윗모드 Task 잔여 개수 조회", description = "두윗모드 Task의 잔여 개수를 조회합니다.")
    @ApiSuccessResponse(description = "두윗모드 Task 잔여 개수 조회 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "잘못된 요청인 경우")})
    @GetMapping("/remained")
    public ResponseEntity<ResponseDto<GetRemainedDowithTaskCountRes>> getRemainedDowithTaskCount() {
        int remainedDowithTaskCount = taskSummaryService.getRemainedDowithTaskCount(AuthUtil.getMemberId());
        return ResponseUtil.createSuccessResponse(new GetRemainedDowithTaskCountRes(remainedDowithTaskCount));
    }
}
