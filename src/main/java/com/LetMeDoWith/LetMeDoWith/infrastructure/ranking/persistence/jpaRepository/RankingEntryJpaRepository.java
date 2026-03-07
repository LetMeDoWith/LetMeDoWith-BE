package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingEntryJpaRepository extends JpaRepository<RankingEntry, Long> {
    List<RankingEntry> findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(
            Long rankingTopicId, Long round);
}
