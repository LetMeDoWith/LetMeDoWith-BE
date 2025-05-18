package com.LetMeDoWith.LetMeDoWith.infrastructure.member.query;

import com.LetMeDoWith.LetMeDoWith.infrastructure.member.query.dto.MemberBadgeQueryDto;

import java.util.List;

public interface BadgeQueryRepository {

    List<MemberBadgeQueryDto> getBadges(String memberId);
}
