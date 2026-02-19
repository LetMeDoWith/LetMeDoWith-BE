package com.LetMeDoWith.LetMeDoWith.application.ranking.service;

import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveMyRankingResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingTopicsResult;
import com.LetMeDoWith.LetMeDoWith.application.ranking.dto.RetrieveRankingsResult;
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

    public RetrieveRankingsResult retrieveRankingsByTopicId(
            Long rankingTopicId, Integer year, Integer month, Integer week, Integer limit) {
        List<RankingsQueryDto> rankings =
                rankingQueryRepository.getRankingsByTopicId(rankingTopicId, year, month, week, limit);
        return RetrieveRankingsResult.from(rankings);
    }

    public RetrieveMyRankingResult retrieveMyRanking(
            String memberId, Long rankingTopicId, Integer year, Integer month, Integer week) {
        RankingsQueryDto ranking = rankingQueryRepository.getMyRanking(memberId, rankingTopicId, year, month, week);
        return RetrieveMyRankingResult.from(ranking);
    }
}
