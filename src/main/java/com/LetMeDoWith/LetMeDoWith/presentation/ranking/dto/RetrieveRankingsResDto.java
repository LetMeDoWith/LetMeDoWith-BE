package com.LetMeDoWith.LetMeDoWith.presentation.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingsResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingsResult.RetrieveRankingResult;
import java.util.List;

public record RetrieveRankingsResDto(List<RetrieveRankingResDto> rankings) {
    public static RetrieveRankingsResDto from(RetrieveRankingsResult result) {
        return new RetrieveRankingsResDto(
                result.rankings().stream().map(RetrieveRankingResDto::from).toList());
    }

    public record RetrieveRankingResDto(
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
