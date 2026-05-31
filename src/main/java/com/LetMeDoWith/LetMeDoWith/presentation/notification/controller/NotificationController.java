package com.LetMeDoWith.LetMeDoWith.presentation.notification.controller;

import com.LetMeDoWith.LetMeDoWith.application.notification.dto.RetrieveNotificationsResult;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.NotificationService;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.RetrieveNotificationService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponsePageDto;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.notification.dto.RetrieveNotificationsResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "알림")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final RetrieveNotificationService retrieveNotificationService;
    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "로그인 회원의 알림 목록을 최신순으로 페이징 조회합니다.")
    @ApiSuccessResponse(description = "알림 목록 조회 성공")
    @GetMapping("")
    public ResponseEntity<ResponsePageDto<RetrieveNotificationsResDto>> retrieveNotifications(
            @Parameter(description = "조회 타입", example = "NORMAL") @RequestParam NotificationType type,
            @ParameterObject Pageable pageable) {
        RetrieveNotificationsResult result =
                retrieveNotificationService.retrieveNotifications(AuthUtil.getMemberId(), type, pageable);
        return ResponseUtil.createSuccessResponse(
                RetrieveNotificationsResDto.from(result), pageable, result.totalCount());
    }

    @Operation(summary = "알림 확인", description = "알림을 확인 처리합니다. 이미 확인한 알림도 200으로 응답합니다.")
    @ApiSuccessResponse(description = "알림 확인 성공")
    @ApiErrorResponses({
        @ApiErrorResponse(status = FailResponseStatus.INVALID_REQUEST, description = "해당 알림 소유 회원이 아닌 경우")
    })
    @PostMapping("/{notificationId}/confirm")
    public ResponseEntity<ResponseDto<Object>> confirmNotification(
            @Parameter(description = "알림 ID", example = "1") @PathVariable Long notificationId) {
        notificationService.confirmNotification(notificationId, AuthUtil.getMemberId());
        return ResponseUtil.createSuccessResponse();
    }
}
