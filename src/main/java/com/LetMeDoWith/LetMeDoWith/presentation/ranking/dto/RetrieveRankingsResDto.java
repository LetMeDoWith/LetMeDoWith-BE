package com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingsResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingsResult.RetrieveRankingResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "랭킹 목록 조회 응답")
public record RetrieveRankingsResDto(@Schema(description = "랭킹 목록") List<RetrieveRankingResDto> rankings) {
    public static RetrieveRankingsResDto from(RetrieveRankingsResult result) {
        return new RetrieveRankingsResDto(
                result.rankings().stream().map(RetrieveRankingResDto::from).toList());
    }

    @Schema(description = "랭킹 정보")
    public record RetrieveRankingResDto(
            @Schema(description = "조회 연도", example = "2026") Integer year,
            @Schema(description = "조회 월", example = "2") Integer month,
            @Schema(description = "조회 주차", example = "3") Integer week,
            @Schema(description = "주제 ID", example = "1") Long topicId,
            @Schema(description = "주제 제목", example = "이번 주 독서왕") String topicTitle,
            @Schema(description = "현재 순위", example = "1") Long currentRank,
            @Schema(description = "이전 순위", example = "2") Long previousRank,
            @Schema(description = "회원 ID", example = "member_123") String memberId,
            @Schema(description = "닉네임", example = "도윗왕") String nickname,
            @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png") String profileImageUrl) {
        public static RetrieveRankingResDto from(RetrieveRankingResult ranking) {
            return new RetrieveRankingResDto(
                    ranking.year(),
                    ranking.month(),
                    ranking.week(),
                    ranking.topicId(),
                    ranking.topicTitle(),
                    ranking.currentRank(),
                    ranking.previousRank(),
                    ranking.memberId(),
                    ranking.nickname(),
                    ranking.profileImageUrl());
        }
    }
}
