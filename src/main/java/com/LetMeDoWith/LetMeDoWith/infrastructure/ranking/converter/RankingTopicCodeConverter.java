package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.converter;

import com.LetMeDoWith.LetMeDoWith.common.converter.AbstractCombinedEnumConverter;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RankingTopicCodeConverter extends AbstractCombinedEnumConverter<RankingTopicCode> {

    public RankingTopicCodeConverter() {
        super(RankingTopicCode.class);
    }
}
