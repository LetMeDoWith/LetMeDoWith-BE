package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import java.util.List;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingTopicsQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;

public interface RankingQueryRepository {
    List<RankingTopicsQueryDto> getRankingTopics();

    List<RankingsQueryDto> getRankingsByTopicId(Long rankingTopicId, Integer year, Integer month, Integer week);

    // 내 랭킹 조회
    RankingsQueryDto getMyRanking(String memberId, Long rankingTopicId, Integer year, Integer month, Integer week);
}
