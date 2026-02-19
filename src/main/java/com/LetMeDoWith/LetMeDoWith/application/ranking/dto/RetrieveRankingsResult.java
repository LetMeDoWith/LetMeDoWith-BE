package com.LetMeDoWith.LetMeDoWith.application.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;
import java.util.List;

public record RetrieveRankingsResult(List<RetrieveRankingResult> rankings) {
    public static RetrieveRankingsResult from(List<RankingsQueryDto> rankings) {
        return new RetrieveRankingsResult(
                rankings.stream().map(RetrieveRankingResult::from).toList());
    }

    public record RetrieveRankingResult(
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
        public static RetrieveRankingResult from(RankingsQueryDto ranking) {
            return new RetrieveRankingResult(
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
