package com.LetMeDoWith.LetMeDoWith.infrastructure.notification.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.domain.notification.enums.NotificationTemplateCode;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class NotificationTemplateCodeConverter extends AbstractCombinedEnumConverter<NotificationTemplateCode> {
    public NotificationTemplateCodeConverter() {
        super(NotificationTemplateCode.class);
    }
}
