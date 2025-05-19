package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.member.query.dto.MemberBadgeQueryDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetrieveBadgesInfoResult {

    private boolean isMemberLazy;
    private List<MemberBadgeQueryDto> badges;

    public static RetrieveBadgesInfoResult of(boolean isLazy, List<MemberBadgeQueryDto> badges) {
        return RetrieveBadgesInfoResult.builder().isMemberLazy(isLazy).badges(badges).build();
    }
}
