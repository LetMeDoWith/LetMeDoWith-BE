package com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberBadgeVO;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import java.util.List;

public interface QBadgeJpaRepository {

    List<MemberBadgeVO> findAllJoinMemberBadge(Long memberId, BadgeStatus badgeStatus);
}
