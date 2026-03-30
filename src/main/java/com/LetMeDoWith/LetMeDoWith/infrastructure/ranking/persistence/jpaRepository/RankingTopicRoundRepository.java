package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingTopicRoundRepository extends JpaRepository<RankingTopicRound, Long> {
    Optional<RankingTopicRound> findFirstByRankingTopicOrderByRoundDesc(RankingTopic rankingTopic);
}
