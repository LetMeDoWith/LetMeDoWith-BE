package com.LetMeDoWith.LetMeDoWith.common.enums.member;

import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowType implements BaseEnum {
    FOLLOWER("FOLLOWER", "팔로워"),
    FOLLOWING("FOLLOWING", "팔로잉");

    private final String code;
    private final String description;

    private static final Map<String, FollowType> CODE_MAP =
            Stream.of(values()).collect(Collectors.toMap(FollowType::getCode, e -> e));

    private static FollowType getEnum(String code) {
        return CODE_MAP.get(code);
    }
}
