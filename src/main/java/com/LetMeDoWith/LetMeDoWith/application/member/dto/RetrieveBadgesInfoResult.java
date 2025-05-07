package com.LetMeDoWith.LetMeDoWith.application.member.dto;

import com.LetMeDoWith.LetMeDoWith.domain.member.dto.MemberBadgeDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetrieveBadgesInfoResult {
    
    private boolean isMemberLazy;
    private List<MemberBadgeDto> badges;
    
    public static RetrieveBadgesInfoResult of(boolean isLazy, List<MemberBadgeDto> badges) {
        return RetrieveBadgesInfoResult.builder()
                                       .isMemberLazy(isLazy)
                                       .badges(badges)
                                       .build();
    }
    
}
