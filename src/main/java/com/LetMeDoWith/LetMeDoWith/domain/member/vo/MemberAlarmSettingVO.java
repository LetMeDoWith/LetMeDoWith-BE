package com.LetMeDoWith.LetMeDoWith.domain.member.vo;

import lombok.Builder;

@Builder
public record MemberAlarmSettingVO(boolean baseAlarmYn, boolean feedbackYn, boolean todoBotYn, boolean marketingYn) {}
