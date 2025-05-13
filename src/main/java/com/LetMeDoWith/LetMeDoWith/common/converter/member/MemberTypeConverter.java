package com.LetMeDoWith.LetMeDoWith.common.converter.member;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class MemberTypeConverter extends AbstractCombinedConverter<MemberType> {

    public MemberTypeConverter() {
        super(MemberType.class);
    }
}
