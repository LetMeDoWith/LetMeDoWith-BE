package com.LetMeDoWith.LetMeDoWith.presentation.task.dto;

import com.LetMeDoWith.LetMeDoWith.application.task.dto.RetrieveDowithTaskLikersResult;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "두윗 Task 좋아요 회원 목록 조회 응답")
public record RetrieveDowithTaskLikersResDto(
        @ArraySchema(
                        arraySchema = @Schema(description = "좋아요를 누른 회원 목록 (최신순)"),
                        schema = @Schema(implementation = RetrieveDowithTaskLikerItemResDto.class))
                List<RetrieveDowithTaskLikerItemResDto> likers) {

    public static RetrieveDowithTaskLikersResDto from(RetrieveDowithTaskLikersResult result) {
        List<RetrieveDowithTaskLikerItemResDto> likers = result.likers().stream()
                .map(liker -> new RetrieveDowithTaskLikerItemResDto(
                        liker.dowithTaskLikeId(), liker.memberId(), liker.nickname(), liker.profileImageUrl()))
                .toList();
        return new RetrieveDowithTaskLikersResDto(likers);
    }

    @Schema(name = "DowithTaskLikerItem", description = "두윗 Task 좋아요 한 건")
    public record RetrieveDowithTaskLikerItemResDto(
            @Schema(description = "좋아요 ID", example = "1") Long dowithTaskLikeId,
            @Schema(description = "회원 ID (TSID)", example = "01234567890123456789012345") String memberId,
            @Schema(description = "닉네임", example = "두윗러") String nickname,
            @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png") String profileImageUrl) {}
}
