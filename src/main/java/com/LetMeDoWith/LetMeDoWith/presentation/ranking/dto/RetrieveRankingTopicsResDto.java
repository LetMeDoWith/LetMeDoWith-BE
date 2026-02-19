package com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto;

import java.util.List;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingTopicsResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingTopicsResult.RetrieveRankingTopicResult;

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
