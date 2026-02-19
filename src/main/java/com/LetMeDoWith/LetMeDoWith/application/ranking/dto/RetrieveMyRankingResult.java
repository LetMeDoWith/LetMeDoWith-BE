package com.LetMeDoWith.LetMeDoWith.application.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;

public record RetrieveMyRankingResult(Integer year,
    Integer month,
    Integer week,
    Long topicId,
    String topicTitle,
    Long currentRank,
    Long previousRank,
    String memberId,
    String nickname,
    String profileImageUrl) {
    public static RetrieveMyRankingResult from(RankingsQueryDto ranking) {
        return new RetrieveMyRankingResult(ranking.year(), ranking.month(), ranking.week(), ranking.topicId(), ranking.topicTitle(), ranking.currentRank(), ranking.previousRank(), ranking.memberId(), ranking.nickname(), ranking.profileImageUrl());
    }
}
