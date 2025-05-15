package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 알람 수신 상태 변경 요청")
public record UpdateMemberNotiSettingReq(
    @Schema(description = "기본 알람 수신 여부") boolean baseAlarmYn,
    @Schema(description = "투두 알림봇 알람 수신 여부") boolean todoBotYn,
    @Schema(description = "잔소리 (피드백) 알람 수신 여부") boolean feedbackYn,
    @Schema(description = "마케팅, 광고성 알람 수신 여부") boolean marketingYn) {
    
}