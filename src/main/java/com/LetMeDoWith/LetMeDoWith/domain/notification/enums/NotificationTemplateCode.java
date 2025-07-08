package com.LetMeDoWith.LetMeDoWith.domain.notification.enums;

import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.converter.NotificationTemplateCodeConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonDeserialize(using = NotificationTemplateCodeConverter.class)
@Schema(enumAsRef = true)
public enum NotificationTemplateCode implements BaseEnum {

    SIGN_UP_COMPLETE("SIGN_UP_COMPLETE", "회원가입 완료 알림 템플릿");

    private final String code;
    private final String description;
}
