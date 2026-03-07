package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingTopicRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class RankingTopicRepositoryImpl implements RankingTopicRepository {

    private final RankingTopicJpaRepository rankingTopicJpaRepository;

    @Override
    public Optional<RankingTopic> getRankingTopic(String title) {
        return rankingTopicJpaRepository.findByTitleAndIsActive(title, Yn.TRUE);
    }

    @Override
    public RankingTopic save(RankingTopic rankingTopic) {
        return rankingTopicJpaRepository.save(rankingTopic);
    }
}
