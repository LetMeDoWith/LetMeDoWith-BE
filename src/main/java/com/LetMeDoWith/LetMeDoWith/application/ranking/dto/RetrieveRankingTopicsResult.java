package com.LetMeDoWith.LetMeDoWith.application.ranking.dto;

import java.util.List;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingTopicsQueryDto;

public record RetrieveRankingTopicsResult(List<RetrieveRankingTopicResult> rankingTopics) {
    public static RetrieveRankingTopicsResult from(List<RankingTopicsQueryDto> rankingTopics) {
        return new RetrieveRankingTopicsResult(rankingTopics.stream()
                .map(RetrieveRankingTopicResult::from)
                .toList());
    }

    public record RetrieveRankingTopicResult(Long id, String title, String description) {
        public static RetrieveRankingTopicResult from(RankingTopicsQueryDto rankingTopic) {
            return new RetrieveRankingTopicResult(rankingTopic.id(), rankingTopic.title(), rankingTopic.description());
        }
    }
}
