package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingTopicRoundJpaRepository extends JpaRepository<RankingTopicRound, Long> {
    Optional<RankingTopicRound> findByRankingTopicIdAndRound(Long rankingTopicId, Long round);
}
