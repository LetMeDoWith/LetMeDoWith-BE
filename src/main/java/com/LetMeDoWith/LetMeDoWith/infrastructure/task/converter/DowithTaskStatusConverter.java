package com.LetMeDoWith.LetMeDoWith.infrastructure.task.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DowithTaskStatusConverter extends AbstractCombinedEnumConverter<DowithTaskStatus> {

    public DowithTaskStatusConverter() {
        super(DowithTaskStatus.class);
    }
}
