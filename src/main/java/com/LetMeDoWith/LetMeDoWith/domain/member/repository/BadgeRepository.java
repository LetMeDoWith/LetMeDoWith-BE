package com.LetMeDoWith.LetMeDoWith.domain.member.repository;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberBadgeVO;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Badge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberBadge;
import java.util.List;
import java.util.Optional;

public interface BadgeRepository {

    void save(List<MemberBadge> memberBadges);

    void save(MemberBadge memberBadge);

    Optional<MemberBadge> getMemberBadge(Long memberId, Badge badge);

    Optional<MemberBadge> getMainMemberBadge(Long memberId);

    Optional<Badge> getBadge(Long badgeId, BadgeStatus badgeStatus);

    List<MemberBadgeVO> getBadges(Long memberId);
}
