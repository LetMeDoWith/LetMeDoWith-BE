package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import lombok.Builder;

@Builder
public record MemberAlarmSettingVO(boolean baseAlarmYn, boolean feedbackYn, boolean todoBotYn, boolean marketingYn) {

    public static MemberAlarmSettingVO fromCommand(UpdateMemberAlarmSettingCommand command) {
        return MemberAlarmSettingVO.builder()
                .baseAlarmYn(command.baseAlarmYn())
                .feedbackYn(command.feedbackYn())
                .todoBotYn(command.todoBotYn())
                .marketingYn(command.marketingYn())
                .build();
    }
}
