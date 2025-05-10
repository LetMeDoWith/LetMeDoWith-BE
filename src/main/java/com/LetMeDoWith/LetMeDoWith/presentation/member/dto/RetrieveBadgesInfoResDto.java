package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberBadgeVO;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
@Builder
public class RetrieveBadgesInfoResDto {
    private Boolean isLazyMember;
    private MainBadge mainBadge;
    private List<Badge> badges;

    @Data
    @Builder
    public static class MainBadge {
        private Long id;
        private String name;
        private String imageUrl;
        private String description;
    }

    @Data
    @Builder
    public static class Badge {
        private Long id;
        private Boolean isAcquired;
        private String name;
        private String imageUrl;
        private String description;
        private String acquireHint;
    }

    public static RetrieveBadgesInfoResDto of(
            Long memberId,
            boolean isLazy,
            @Nullable MemberBadgeVO mainBadgeVO,
            List<MemberBadgeVO> badgeVOs) {

        List<Badge> badgesResult = new ArrayList<>();
        badgeVOs.forEach(
                e ->
                        badgesResult.add(
                                Badge.builder()
                                        .id(e.getBadgeId())
                                        .isAcquired(e.getMemberBadgeId() != null)
                                        .name(e.getName())
                                        .imageUrl(e.getImageUrl())
                                        .description(e.getDescription())
                                        .acquireHint(e.getAcquireHint())
                                        .build()));

        return RetrieveBadgesInfoResDto.builder()
                .isLazyMember(isLazy)
                .mainBadge(
                        mainBadgeVO == null
                                ? null
                                : MainBadge.builder()
                                        .id(mainBadgeVO.getBadgeId())
                                        .name(mainBadgeVO.getName())
                                        .imageUrl(mainBadgeVO.getImageUrl())
                                        .description(mainBadgeVO.getDescription())
                                        .build())
                .badges(badgesResult)
                .build();
    }
}
