package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingEntryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingEntryJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class RankingEntryRepositoryImpl implements RankingEntryRepository {

    private final RankingEntryJpaRepository rankingEntryJpaRepository;

    @Override
    public List<RankingEntry> getEntriesByTopicAndRound(Long topicId, Long round) {
        return rankingEntryJpaRepository.findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(
                topicId, round);
    }

    @Override
    public List<RankingEntry> saveAll(List<RankingEntry> rankingEntries) {
        return rankingEntryJpaRepository.saveAll(rankingEntries);
    }
}
