package com.LetMeDoWith.LetMeDoWith.common.converter;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NotificationTemplateCodeConverter extends AbstractCombinedEnumConverter<NotificationTemplateCode> {

    public NotificationTemplateCodeConverter() {
        super(NotificationTemplateCode.class);
    }
}
