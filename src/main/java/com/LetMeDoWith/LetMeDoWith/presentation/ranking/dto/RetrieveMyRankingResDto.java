package com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveMyRankingResult;

public record RetrieveMyRankingResDto(
        Integer year,
        Integer month,
        Integer week,
        Long topicId,
        String topicTitle,
        Long currentRank,
        Long previousRank,
        String memberId,
        String nickname,
        String profileImageUrl) {
    public static RetrieveMyRankingResDto from(RetrieveMyRankingResult result) {
        return new RetrieveMyRankingResDto(
                result.year(),
                result.month(),
                result.week(),
                result.topicId(),
                result.topicTitle(),
                result.currentRank(),
                result.previousRank(),
                result.memberId(),
                result.nickname(),
                result.profileImageUrl());
    }
}
