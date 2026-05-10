package com.LetMeDoWith.LetMeDoWith.presentation.member.controller;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.RetrieveBadgesInfoResult;
import com.LetMeDoWith.LetMeDoWith.application.member.service.BadgeService;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.common.util.ResponseUtil;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.query.dto.MemberBadgeQueryDto;
import com.LetMeDoWith.LetMeDoWith.presentation.member.dto.RetrieveBadgesInfoResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member Badge", description = "회원 뱃지")
@RestController
@RequestMapping("/api/v1/members/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @Operation(summary = "뱃지 정보 조회", description = "유져의 뱃지 정보(소유 뱃지, 획득 필요 뱃지, 힌트 등) 조회합니다.")
    @GetMapping("")
    public ResponseEntity retrieveBadgesInfo() {

        String memberId = AuthUtil.getMemberId();
        RetrieveBadgesInfoResult result = badgeService.retrieveBadgesInfo(memberId);

        MemberBadgeQueryDto mainBadge = result.getBadges().stream()
                .filter(e -> Yn.TRUE.equals(e.getIsMain()))
                .findFirst()
                .orElse(null);

        return ResponseUtil.createSuccessResponse(
                RetrieveBadgesInfoResDto.of(memberId, result.isMemberLazy(), mainBadge, result.getBadges()));
    }

    @Operation(summary = "대표 뱃지 등록", description = "특정 뱃지를 유져의 대표 뱃지로 등록합니다.")
    @PutMapping("/{badgeId}/main")
    public ResponseEntity updateMainBadge(
            @Parameter(description = "대표로 등록할 뱃지 ID", example = "1") @PathVariable Long badgeId) {

        String memberId = AuthUtil.getMemberId();
        badgeService.updateMainBadge(memberId, badgeId);

        return ResponseUtil.createSuccessResponse();
    }
}
