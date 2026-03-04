package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingTopicRoundRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicRoundJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class RankingTopicRoundRepositoryImpl implements RankingTopicRoundRepository {

    private final RankingTopicRoundJpaRepository rankingTopicRoundJpaRepository;

    @Override
    public RankingTopicRound save(RankingTopicRound rankingTopicRound) {
        return rankingTopicRoundJpaRepository.save(rankingTopicRound);
    }
}
