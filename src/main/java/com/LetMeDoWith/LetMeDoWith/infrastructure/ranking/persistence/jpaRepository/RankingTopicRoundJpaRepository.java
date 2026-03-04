package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingTopicRoundJpaRepository extends JpaRepository<RankingTopicRound, Long> {}
