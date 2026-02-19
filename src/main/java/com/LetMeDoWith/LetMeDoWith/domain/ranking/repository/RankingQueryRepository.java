package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingTopicsQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;
import java.util.List;

public interface RankingQueryRepository {
    List<RankingTopicsQueryDto> getRankingTopics();

    List<RankingsQueryDto> getRankingsByTopicId(
            Long rankingTopicId, Integer year, Integer month, Integer week, Integer limit);

    // 내 랭킹 조회
    RankingsQueryDto getMyRanking(String memberId, Long rankingTopicId, Integer year, Integer month, Integer week);
}
