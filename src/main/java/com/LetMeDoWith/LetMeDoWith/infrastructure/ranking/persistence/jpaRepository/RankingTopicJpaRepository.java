package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingTopicJpaRepository extends JpaRepository<RankingTopic, Long> {
    Optional<RankingTopic> findByTitleAndIsActive(String title, Yn isActive);
}
