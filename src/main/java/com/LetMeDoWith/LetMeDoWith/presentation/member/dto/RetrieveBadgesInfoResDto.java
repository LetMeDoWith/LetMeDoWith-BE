package com.LetMeDoWith.LetMeDoWith.presentation.member.dto;

import com.LetMeDoWith.LetMeDoWith.infrastructure.member.query.dto.MemberBadgeQueryDto;
import io.swagger.v3.oas.annotations.media.Schema;
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

    public static RetrieveBadgesInfoResDto of(
            String memberId,
            boolean isLazy,
            @Nullable MemberBadgeQueryDto mainBadgeVO,
            List<MemberBadgeQueryDto> badgeVOs) {

        List<Badge> badgesResult = new ArrayList<>();
        badgeVOs.forEach(e -> badgesResult.add(Badge.builder()
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

        @Schema(description = "뱃지 ID", example = "1")
        private Long id;

        @Schema(description = "획득 여부", example = "true")
        private Boolean isAcquired;

        @Schema(description = "뱃지 이름", example = "1개월 챌린저")
        private String name;

        @Schema(description = "뱃지 이미지 URL")
        private String imageUrl;

        @Schema(description = "뱃지 설명", example = "뱃지에 대한 설명")
        private String description;

        @Schema(description = "뱃지 획득 방법 힌트", example = "뱃지 공개전 부가 설명")
        private String acquireHint;
    }
}
