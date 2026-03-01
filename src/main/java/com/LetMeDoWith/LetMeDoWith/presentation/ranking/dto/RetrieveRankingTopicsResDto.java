package com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingTopicsResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingTopicsResult.RetrieveRankingTopicResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "랭킹 주제 목록 조회 응답")
public record RetrieveRankingTopicsResDto(
        @Schema(description = "랭킹 주제 목록") List<RetrieveRankingTopicResDto> rankingTopics) {
    public static RetrieveRankingTopicsResDto from(RetrieveRankingTopicsResult result) {
        return new RetrieveRankingTopicsResDto(result.rankingTopics().stream()
                .map(RetrieveRankingTopicResDto::from)
                .toList());
    }

    @Schema(description = "랭킹 주제")
    public record RetrieveRankingTopicResDto(
            @Schema(description = "주제 ID", example = "1") Long id,
            @Schema(description = "주제 제목", example = "이번 주 독서왕") String title,
            @Schema(description = "주제 설명", example = "한 주 동안 가장 많이 독서한 사용자를 확인해요.") String description,
            @Schema(description = "현재 조회 회차", example = "3") Long currentRound) {
        public static RetrieveRankingTopicResDto from(RetrieveRankingTopicResult rankingTopic) {
            return new RetrieveRankingTopicResDto(
                    rankingTopic.id(), rankingTopic.title(), rankingTopic.description(), rankingTopic.currentRound());
        }
    }
}
