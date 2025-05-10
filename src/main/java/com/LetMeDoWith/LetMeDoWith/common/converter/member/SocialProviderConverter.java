package com.LetMeDoWith.LetMeDoWith.common.converter.member;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class SocialProviderConverter extends AbstractCombinedConverter<SocialProvider> {

    public SocialProviderConverter() {
        super(SocialProvider.class);
    }
}
