package com.LetMeDoWith.LetMeDoWith.infrastructure.member.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class SocialProviderConverter extends AbstractCombinedEnumConverter<SocialProvider> {

    public SocialProviderConverter() {
        super(SocialProvider.class);
    }
}
