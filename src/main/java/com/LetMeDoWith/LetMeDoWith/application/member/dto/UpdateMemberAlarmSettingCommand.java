package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.UpdateMemberNotiSettingReqDto;
import lombok.Builder;

@Builder
public record UpdateMemberAlarmSettingCommand(
        boolean baseAlarmYn, boolean todoBotYn, boolean feedbackYn, boolean marketingYn) {

    public static UpdateMemberAlarmSettingCommand fromReq(UpdateMemberNotiSettingReqDto req) {
        return UpdateMemberAlarmSettingCommand.builder()
                .baseAlarmYn(req.baseAlarmYn())
                .todoBotYn(req.todoBotYn())
                .feedbackYn(req.feedbackYn())
                .marketingYn(req.marketingYn())
                .build();
    }
}
