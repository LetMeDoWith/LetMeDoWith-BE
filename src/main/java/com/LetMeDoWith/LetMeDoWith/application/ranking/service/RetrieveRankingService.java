package com.LetMeDoWith.LetMeDoWith.application.ranking.service;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveMyRankingResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingTopicsResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingsResult;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingTopicsQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveRankingService {

    private final RankingQueryRepository rankingQueryRepository;

    public RetrieveRankingTopicsResult retrieveRankingTopics() {
        List<RankingTopicsQueryDto> rankingTopics = rankingQueryRepository.getRankingTopics();
        return RetrieveRankingTopicsResult.from(rankingTopics);
    }

    public RetrieveRankingsResult retrieveRankingsByTopicId(Long rankingTopicId, Long round, Integer limit) {
        validateTopicRound(rankingTopicId, round);
        List<RankingsQueryDto> rankings = rankingQueryRepository.getRankingsByTopicId(rankingTopicId, round, limit);
        return RetrieveRankingsResult.from(rankings);
    }

    public RetrieveMyRankingResult retrieveMyRanking(String memberId, Long rankingTopicId, Long round) {
        validateTopicRound(rankingTopicId, round);
        return rankingQueryRepository
                .getMyRanking(memberId, rankingTopicId, round)
                .map(ranking -> RetrieveMyRankingResult.from(round, rankingTopicId, ranking.topicTitle(), ranking))
                .orElseGet(() -> RetrieveMyRankingResult.empty(round, rankingTopicId, null));
    }

    private void validateTopicRound(Long rankingTopicId, Long round) {
        if (!rankingQueryRepository.existsTopicRound(rankingTopicId, round)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
    }
}
