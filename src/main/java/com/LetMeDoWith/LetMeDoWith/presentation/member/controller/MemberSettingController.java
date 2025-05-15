package com.LetMeDoWith.LetMeDoWith.presentation.member.controller;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.UpdateMemberAlarmSettingCommand;
import com.LetMeDoWith.LetMeDoWith.application.member.service.MemberSettingService;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponse;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiErrorResponses;
import com.LetMeDoWith.LetMeDoWith.common.annotation.ApiSuccessResponse;
import com.LetMeDoWith.LetMeDoWith.common.dto.ResponseDto;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.UpdateMemberNotiSettingReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member Setting", description = "회원 설정")
@RestController
@RequestMapping("/api/v1/members/settings")
@RequiredArgsConstructor
public class MemberSettingController {

    private final MemberSettingService memberSettingService;

    /**
     * 유저의 푸쉬 알람 수신 상태를 변경한다.
     *
     * @param req 푸쉬 별 알림 수신 상태
     * @return 성공 응답
     */
    @Operation(summary = "알림 수신 상태 변경", description = "회원의 알림 수신 상태를 변경합니다.")
    @ApiSuccessResponse(description = "회원 알림 수신상태 변경 성공")
    @ApiErrorResponses({@ApiErrorResponse(status = FailResponseStatus.MEMBER_NOT_EXIST)})
    @PutMapping("/notification")
    public <T> ResponseEntity<ResponseDto<T>> updateNotificationSetting(
            @RequestBody UpdateMemberNotiSettingReq req) {
        memberSettingService.updateAlarmSetting(UpdateMemberAlarmSettingCommand.fromReq(req));

        return ResponseUtil.createSuccessResponse();
    }
}
