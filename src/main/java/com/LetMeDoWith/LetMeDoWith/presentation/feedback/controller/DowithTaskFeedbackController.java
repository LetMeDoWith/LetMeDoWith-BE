package com.LetMeDoWith.LetMeDoWith.presentation.feedback.controller;

import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveReceivedTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveSentTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.dto.RetrieveTaskFeedbackResult;
import com.LetMeDoWith.LetMeDoWith.application.feedback.service.CreateDowithTaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.application.feedback.service.RetrieveTaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.application.feedback.service.UpdateDowithTaskFeedbackService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.CreateDowithFeedbackReqDto;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveDowithTaskFeedbacksResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveReceivedDowithTaskFeedbacksResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveSentDowithTaskFeedbacksResDto;
import com.LetMeDoWith.LetMeDoWith.presentation.feedback.dto.RetrieveTaskFeedbackTemplatesResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "DowithTask Feedback", description = "잔소리")
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class DowithTaskFeedbackController {

    private final CreateDowithTaskFeedbackService createDowithTaskFeedbackService;
    private final UpdateDowithTaskFeedbackService updateDowithTaskFeedbackService;
    private final RetrieveTaskFeedbackService retrieveTaskFeedbackService;

    @Operation(summary = "두윗 태스크 잔소리 생성", description = "두윗모드 잔소리를 생성합니다.")
    @ApiSuccessResponse(description = "두윗모드 잔소리 생성 성공. 본 API는 생성 성공 여부만 반환합니다.")
    @ApiErrorResponses({
        @ApiErrorResponse(status = FailResponseStatus.MEMBER_NOT_EXIST, description = "존재하지 않는 회원입니다."),
        @ApiErrorResponse(
                status = FailResponseStatus.INVALID_REQUEST,
                description = "잘못된 요청입니다. (예: 잔소리 불가능한 상태에서 생성 요청하는 경우 등)")
    })
    @PostMapping("")
    public ResponseEntity<ResponseDto<Object>> createDowithFeedback(
            @Valid @RequestBody CreateDowithFeedbackReqDto req) {
        String memberId = AuthUtil.getMemberId();

        createDowithTaskFeedbackService.createDowithFeedback(
                memberId, req.dowithTaskId(), req.taskFeedbackTemplateId());

        return ResponseUtil.createSuccessResponse();
    }

    @Operation(summary = "Dowith Task 잔소리 조회", description = "Dowith Task에 보내진 잔소리를 조회합니다.")
    @ApiSuccessResponse(description = "Dowith Task 잔소리 조회 성공.")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST)})
    @GetMapping("/dowith-task/{dowithTaskId}")
    public ResponseEntity<ResponsePageDto<RetrieveDowithTaskFeedbacksResDto>> retrieveDowithTaskFeedbacks(
            @PathVariable Long dowithTaskId,
            @Parameter(description = "잔소리 템플릿 ID (optional). 지정 시 해당 템플릿의 잔소리만 조회합니다.") @RequestParam(required = false)
                    Long feedbackTemplateId,
            @ParameterObject Pageable pageable) {
        RetrieveTaskFeedbackResult result =
                retrieveTaskFeedbackService.retrieveTaskFeedbacksByTaskId(dowithTaskId, pageable, feedbackTemplateId);
        return ResponseUtil.createSuccessResponse(
                RetrieveDowithTaskFeedbacksResDto.from(result), pageable, result.totalCount());
    }

    @Operation(
            summary = "보낸 잔소리 조회",
            description = "유저가 보낸 잔소리 목록을 조회합니다. 잔소리 대상 두윗모드 Task의 상태(dowithTaskStatus)를 포함합니다.")
    @ApiSuccessResponse(description = "보낸 잔소리 목록 조회 성공.")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST)})
    @GetMapping("/send")
    public ResponseEntity<ResponsePageDto<RetrieveSentDowithTaskFeedbacksResDto>> retrieveSendFeedbacks(
            @ParameterObject Pageable pageable) {
        RetrieveSentTaskFeedbackResult result = retrieveTaskFeedbackService.retrieveSendFeedbacks(pageable);
        return ResponseUtil.createSuccessResponse(
                RetrieveSentDowithTaskFeedbacksResDto.from(result), pageable, result.totalCount());
    }

    @Operation(summary = "받은 잔소리 조회", description = "유저가 받은 잔소리 목록을 조회합니다. 잔소리를 받은 시각(receivedAt)을 포함합니다.")
    @ApiSuccessResponse(description = "받은 잔소리 목록 조회 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST)})
    @GetMapping("/received")
    public ResponseEntity<ResponsePageDto<RetrieveReceivedDowithTaskFeedbacksResDto>> retrieveReceivedFeedbacks(
            @ParameterObject Pageable pageable) {
        RetrieveReceivedTaskFeedbackResult result = retrieveTaskFeedbackService.retrieveReceivedFeedbacks(pageable);
        return ResponseUtil.createSuccessResponse(
                RetrieveReceivedDowithTaskFeedbacksResDto.from(result), pageable, result.totalCount());
    }

    @Operation(summary = "잔소리 템플릿 목록 조회", description = "국가 코드(CountryCode)에 따라 잔소리 템플릿 목록을 조회합니다.")
    @ApiSuccessResponse(description = "잔소리 템플릿 목록 조회 성공. 템플릿 리스트를 반환합니다.")
    @GetMapping("/templates")
    public ResponseEntity<ResponseDto<RetrieveTaskFeedbackTemplatesResDto>> getTemplates(
            @Parameter(description = "국가 코드", required = true, example = "KR") @RequestParam CountryCode language) {
        RetrieveTaskFeedbackTemplatesResDto resDto = RetrieveTaskFeedbackTemplatesResDto.from(
                retrieveTaskFeedbackService.retrieveTaskFeedbackTemplates(language));
        return ResponseUtil.createSuccessResponse(resDto);
    }

    @Operation(summary = "두윗 태스크 잔소리 확인", description = "두윗모드 잔소리를 확인합니다.")
    @ApiSuccessResponse(description = "두윗모드 잔소리 확인 성공. 본 API는 확인 성공 여부만 반환합니다.")
    @PatchMapping("/{feedbackId}/check")
    public ResponseEntity<ResponseDto<Object>> checkDowithTaskFeedback(@PathVariable("feedbackId") Long feedbackId) {
        updateDowithTaskFeedbackService.checkDowithFeedbacks(List.of(feedbackId));
        return ResponseUtil.createSuccessResponse();
    }
}
