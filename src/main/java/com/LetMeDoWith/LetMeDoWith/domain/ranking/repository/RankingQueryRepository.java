package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingTopicsQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.dto.RankingsQueryDto;
import java.util.List;
import java.util.Optional;

public interface RankingQueryRepository {
    List<RankingTopicsQueryDto> getRankingTopics();

    boolean existsTopicRound(Long rankingTopicId, Long round);

    List<RankingsQueryDto> getRankingsByTopicId(Long rankingTopicId, Long round, Integer limit);

    // 내 랭킹 조회
    Optional<RankingsQueryDto> getMyRanking(String memberId, Long rankingTopicId, Long round);
}
