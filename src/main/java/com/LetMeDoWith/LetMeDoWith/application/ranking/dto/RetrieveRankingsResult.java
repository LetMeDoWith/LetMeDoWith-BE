package com.LetMeDoWith.LetMeDoWith.application.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;
import java.util.List;

public record RetrieveRankingsResult(List<RetrieveRankingResult> rankings) {
    public static RetrieveRankingsResult from(List<RankingsQueryDto> rankings) {
        return new RetrieveRankingsResult(
                rankings.stream().map(RetrieveRankingResult::from).toList());
    }

    public record RetrieveRankingResult(
            Long round,
            Long topicId,
            String topicTitle,
            Long currentRank,
            Long previousRank,
            String memberId,
            String nickname,
            String profileImageUrl) {
        public static RetrieveRankingResult from(RankingsQueryDto ranking) {
            return new RetrieveRankingResult(
                    ranking.round(),
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
