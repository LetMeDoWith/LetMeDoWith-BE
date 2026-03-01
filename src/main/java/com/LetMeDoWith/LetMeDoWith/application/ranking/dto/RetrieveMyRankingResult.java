package com.LetMeDoWith.LetMeDoWith.application.ranking.dto;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;

public record RetrieveMyRankingResult(Long round, Long topicId, String topicTitle, MyRankingDto myRanking) {
    public static RetrieveMyRankingResult from(Long round, Long topicId, String topicTitle, RankingsQueryDto ranking) {
        return new RetrieveMyRankingResult(round, topicId, topicTitle, MyRankingDto.from(ranking));
    }

    public static RetrieveMyRankingResult empty(Long round, Long topicId, String topicTitle) {
        return new RetrieveMyRankingResult(round, topicId, topicTitle, null);
    }

    public record MyRankingDto(
            Long currentRank, Long previousRank, String memberId, String nickname, String profileImageUrl) {
        public static MyRankingDto from(RankingsQueryDto ranking) {
            return new MyRankingDto(
                    ranking.currentRank(),
                    ranking.previousRank(),
                    ranking.memberId(),
                    ranking.nickname(),
                    ranking.profileImageUrl());
        }
    }
}
