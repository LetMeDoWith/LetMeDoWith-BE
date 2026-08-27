package com.LetMeDoWith.LetMeDoWith.common.converter;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YnConverter extends AbstractCombinedEnumConverter<Yn> {

    public YnConverter() {
        super(Yn.class);
    }
}
