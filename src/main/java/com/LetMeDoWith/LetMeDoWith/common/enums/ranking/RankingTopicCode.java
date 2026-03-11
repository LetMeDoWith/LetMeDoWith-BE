package com.LetMeDoWith.LetMeDoWith.common.enums.ranking;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RankingTopicCode implements BaseEnum {
    FEEDBACK_KING("FEEDBACK_KING", "피드백 킹"),
    JAKSIM_SAMILER("JAKSIM_SAMILER", "작심삼일러");

    private final String code;
    private final String description;
}
