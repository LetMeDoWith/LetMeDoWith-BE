package com.LetMeDoWith.LetMeDoWith.application.member.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.BADGE_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.LAZY_NOT_AVAIL_UPDATE_MAIN_BADGE;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.MEMBER_BADGE_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.MEMBER_NOT_EXIST_BADGE;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.RetrieveBadgesInfoResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Badge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberBadge;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.BadgeRepository;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.query.BadgeQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.query.dto.MemberBadgeQueryDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final BadgeQueryRepository badgeQueryRepository;

    private final MemberRepository memberRepository;

    /**
     * 유져의 뱃지 리스트 조회
     *
     * @param memberId
     * @return
     */
    public RetrieveBadgesInfoResult retrieveBadgesInfo(Long memberId) {

        Member member =
                memberRepository
                        .getNormalStatusMember(memberId)
                        .orElseThrow(() -> new RestApiException(MEMBER_NOT_EXIST_BADGE));

        List<MemberBadgeQueryDto> badges = badgeQueryRepository.getBadges(memberId);

        return RetrieveBadgesInfoResult.of(member.isLazyBadgeAcquireLevel(), badges);
    }

    /**
     * 대표 뱃지로 설정
     *
     * @param memberId
     * @param badgeId
     */
    @Transactional
    public void updateMainBadge(Long memberId, Long badgeId) {

        Member member =
                memberRepository
                        .getMember(memberId, MemberStatus.NORMAL)
                        .orElseThrow(() -> new RestApiException(MEMBER_NOT_EXIST_BADGE));

        if (member.isLazyBadgeAcquireLevel()) {
            throw new RestApiException(LAZY_NOT_AVAIL_UPDATE_MAIN_BADGE);
        }

        Badge newMainBadge =
                badgeRepository
                        .getBadge(badgeId, BadgeStatus.ACTIVE)
                        .orElseThrow(() -> new RestApiException(BADGE_NOT_EXIST));

        // 기존 Main Badge cancel
        Optional<MemberBadge> mainMemberBadge = badgeRepository.getMainMemberBadge(memberId);
        mainMemberBadge.ifPresent(MemberBadge::cancelMainBadge);

        // 새로운 Main Badge 등록
        MemberBadge memberBadge =
                badgeRepository
                        .getMemberBadge(memberId, newMainBadge)
                        .orElseThrow(() -> new RestApiException(MEMBER_BADGE_NOT_EXIST));
        memberBadge.registerToMainBadge();
    }
}
