package com.LetMeDoWith.LetMeDoWith.infrastructure.member.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.FollowType;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class FollowTypeConverter extends AbstractCombinedEnumConverter<FollowType> {

    public FollowTypeConverter() {
        super(FollowType.class);
    }
}
