package com.LetMeDoWith.LetMeDoWith.domain.member.repository;

import com.LetMeDoWith.LetMeDoWith.domain.member.dto.MemberBadgeDto;
import java.util.List;

public interface BadgeQueryRepository {
    
    List<MemberBadgeDto> getBadges(Long memberId);
}
