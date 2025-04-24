package com.LetMeDoWith.LetMeDoWith.common.converter.task;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter(autoApply = true)
@Component
public class CountryCodeConverter extends AbstractCombinedConverter<CountryCode> {
    
    public CountryCodeConverter() {
        super(CountryCode.class);
    }
}