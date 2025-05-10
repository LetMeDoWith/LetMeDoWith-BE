package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetBadgesInfoResult {

    private boolean isMemberLazy;
    private List<MemberBadgeVO> badges;

    public static GetBadgesInfoResult of(boolean isLazy, List<MemberBadgeVO> badges) {
        return GetBadgesInfoResult.builder().isMemberLazy(isLazy).badges(badges).build();
    }
}
