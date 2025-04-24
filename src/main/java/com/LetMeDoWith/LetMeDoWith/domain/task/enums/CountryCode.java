package com.LetMeDoWith.LetMeDoWith.domain.task.enums;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountryCode implements BaseEnum {
    KR("KR", "대한민국"),
    US("US", "미국"),
    JP("JP", "일본"),
    CN("CN", "중국"),
    UK("UK", "영국");
    
    private final String code;
    private final String description;
}