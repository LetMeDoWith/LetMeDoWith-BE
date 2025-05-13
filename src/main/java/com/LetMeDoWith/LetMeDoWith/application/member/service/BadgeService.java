package com.LetMeDoWith.LetMeDoWith.application.member.service;

import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.BADGE_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.LAZY_NOT_AVAIL_UPDATE_MAIN_BADGE;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.MEMBER_BADGE_NOT_EXIST;
import static com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus.MEMBER_NOT_EXIST_BADGE;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.GetBadgesInfoResult;
import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberBadgeVO;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Badge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberBadge;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.BadgeRepository;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final MemberRepository memberRepository;

    /**
     * 유져의 뱃지 리스트 조회
     *
     * @param memberId
     * @return
     */
    public GetBadgesInfoResult getBadgesInfo(String memberId) {

        Member member =
                memberRepository
                        .getNormalStatusMember(memberId)
                        .orElseThrow(() -> new RestApiException(MEMBER_NOT_EXIST_BADGE));

        List<MemberBadgeVO> badges = badgeRepository.getBadges(memberId);

        return GetBadgesInfoResult.of(member.isLazyBadgeAcquireLevel(), badges);
    }

    /**
     * 대표 뱃지로 설정
     *
     * @param memberId
     * @param badgeId
     */
    @Transactional
    public void updateMainBadge(String memberId, Long badgeId) {

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
        if (mainMemberBadge.isPresent()) {
            mainMemberBadge.get().cancelMainBadge();
        }

        // 새로운 Main Badge 등록
        MemberBadge memberBadge =
                badgeRepository
                        .getMemberBadge(memberId, newMainBadge)
                        .orElseThrow(() -> new RestApiException(MEMBER_BADGE_NOT_EXIST));
        memberBadge.registerToMainBadge();
    }
}
