package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingTopicJpaRepository extends JpaRepository<RankingTopic, Long> {
    Optional<RankingTopic> findByCodeAndIsActive(RankingTopicCode code, Yn isActive);
}
