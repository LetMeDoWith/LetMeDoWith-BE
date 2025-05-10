package com.LetMeDoWith.LetMeDoWith.common.converter.member;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.FollowType;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class FollowTypeConverter extends AbstractCombinedConverter<FollowType> {

    public FollowTypeConverter() {
        super(FollowType.class);
    }
}
