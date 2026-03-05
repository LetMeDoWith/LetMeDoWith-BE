package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingEntryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RankingRepositoryImpl implements RankingRepository {

    private final RankingTopicJpaRepository rankingTopicJpaRepository;
    private final RankingTopicRoundRepository rankingTopicRoundRepository;
    private final RankingEntryJpaRepository rankingEntryJpaRepository;

    @Override
    public Optional<RankingTopic> getRankingTopic(RankingTopicCode rankingTopic) {
        return rankingTopicJpaRepository.findByCodeAndIsActive(rankingTopic, Yn.TRUE);
    }

    @Override
    public Optional<RankingTopicRound> getLatestRankingTopicRound(RankingTopic rankingTopic) {
        return rankingTopicRoundRepository.findFirstByRankingTopicOrderByRoundDesc(rankingTopic);
    }

    @Override
    public RankingTopicRound save(RankingTopicRound rankingTopicRound) {
        return rankingTopicRoundRepository.save(rankingTopicRound);
    }

    @Override
    public void save(List<RankingEntry> rankingEntries) {
        rankingEntryJpaRepository.saveAll(rankingEntries);
    }
}
