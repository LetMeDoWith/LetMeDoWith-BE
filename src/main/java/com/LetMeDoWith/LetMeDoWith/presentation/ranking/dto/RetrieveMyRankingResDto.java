package com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveMyRankingResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveMyRankingResult.MyRankingDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 랭킹 조회 응답")
public record RetrieveMyRankingResDto(
        @Schema(description = "조회 회차", example = "3") Long round,
        @Schema(description = "주제 ID", example = "1") Long topicId,
        @Schema(description = "주제 제목", example = "이번 주 독서왕") String topicTitle,
        @Schema(description = "내 랭킹 정보. 데이터가 없으면 null", nullable = true) MyRankingResDto myRanking) {
    public static RetrieveMyRankingResDto from(RetrieveMyRankingResult result) {
        return new RetrieveMyRankingResDto(
                result.round(), result.topicId(), result.topicTitle(), MyRankingResDto.from(result.myRanking()));
    }

    @Schema(description = "내 랭킹 상세 정보")
    public record MyRankingResDto(
            @Schema(description = "현재 순위", example = "4") Long currentRank,
            @Schema(description = "이전 순위", example = "5") Long previousRank,
            @Schema(description = "회원 ID", example = "member_123") String memberId,
            @Schema(description = "닉네임", example = "도윗왕") String nickname,
            @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png") String profileImageUrl) {
        public static MyRankingResDto from(MyRankingDto myRanking) {
            if (myRanking == null) {
                return null;
            }

            return new MyRankingResDto(
                    myRanking.currentRank(),
                    myRanking.previousRank(),
                    myRanking.memberId(),
                    myRanking.nickname(),
                    myRanking.profileImageUrl());
        }
    }
}
