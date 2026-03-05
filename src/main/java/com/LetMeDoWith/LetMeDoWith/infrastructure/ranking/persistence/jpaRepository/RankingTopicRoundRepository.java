package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingTopicRoundRepository extends JpaRepository<RankingTopicRound, Long> {
    Optional<RankingTopicRound> findFirstByRankingTopicOrderByRoundDesc(RankingTopic rankingTopic);
}
