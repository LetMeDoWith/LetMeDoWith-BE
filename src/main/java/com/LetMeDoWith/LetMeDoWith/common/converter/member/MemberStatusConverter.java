package com.LetMeDoWith.LetMeDoWith.common.converter.member;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class MemberStatusConverter extends AbstractCombinedConverter<MemberStatus> {

    public MemberStatusConverter() {
        super(MemberStatus.class);
    }
}
