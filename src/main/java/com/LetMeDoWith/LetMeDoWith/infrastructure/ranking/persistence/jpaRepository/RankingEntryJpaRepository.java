package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingEntryJpaRepository extends JpaRepository<RankingEntry, Long> {
=======
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingEntryJpaRepository extends JpaRepository<RankingEntry, Long> {
    List<RankingEntry> findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(
            Long rankingTopicId, Long round);
>>>>>>> 016c75020c13bf395fc8ac93665c3efbbed050bb
}
