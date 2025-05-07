package com.LetMeDoWith.LetMeDoWith.domain.member.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import lombok.Data;

@Data
public class MemberBadgeDto {
    
    private Long memberBadgeId;
    private Long memberId;
    private Yn isMain;
    
    private Long badgeId;
    private BadgeStatus badgeStatus;
    private String name;
    private String description;
    private String acquireHint;
    private String imageUrl;
    private int sortOrder;
    
}
