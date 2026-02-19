package com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingTopicsResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingTopicsResult.RetrieveRankingTopicResult;
import java.util.List;

public record RetrieveRankingTopicsResDto(List<RetrieveRankingTopicResDto> rankingTopics) {
    public static RetrieveRankingTopicsResDto from(RetrieveRankingTopicsResult result) {
        return new RetrieveRankingTopicsResDto(result.rankingTopics().stream()
                .map(RetrieveRankingTopicResDto::from)
                .toList());
    }

    public record RetrieveRankingTopicResDto(Long id, String title, String description) {
        public static RetrieveRankingTopicResDto from(RetrieveRankingTopicResult rankingTopic) {
            return new RetrieveRankingTopicResDto(rankingTopic.id(), rankingTopic.title(), rankingTopic.description());
        }
    }
}
