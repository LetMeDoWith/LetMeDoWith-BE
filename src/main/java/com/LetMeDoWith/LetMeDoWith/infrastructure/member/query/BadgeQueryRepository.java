package com.LetMeDoWith.LetMeDoWith.infrastructure.member.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.member.query.dto.MemberBadgeDto;
import java.util.List;

public interface BadgeQueryRepository {
    
    List<MemberBadgeDto> getBadges(Long memberId);
}
