package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class CountryCodeConverter extends AbstractCombinedEnumConverter<CountryCode> {

    public CountryCodeConverter() {
        super(CountryCode.class);
    }
}
