package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingEntryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicRoundJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class RankingRepositoryImpl implements RankingRepository {

    private final RankingTopicJpaRepository rankingTopicJpaRepository;
    private final RankingTopicRoundJpaRepository rankingTopicRoundJpaRepository;
    private final RankingEntryJpaRepository rankingEntryJpaRepository;

    @Override
    public Optional<RankingTopic> getRankingTopic(RankingTopicCode code, Yn isActive) {
        return rankingTopicJpaRepository.findByCodeAndIsActive(code, isActive);
    }

    @Override
    public Optional<RankingTopic> getRankingTopic(String title, Yn isActive) {
        return rankingTopicJpaRepository.findByTitleAndIsActive(title, isActive);
    }

    @Override
    public List<RankingEntry> getRankingEntries(Long topicId, Long round) {
        return rankingEntryJpaRepository.findAllByRankingTopicRoundRankingTopicIdAndRankingTopicRoundRound(
                topicId, round);
    }

    @Override
    public RankingTopic save(RankingTopic rankingTopic) {
        return rankingTopicJpaRepository.save(rankingTopic);
    }

    @Override
    public RankingTopicRound save(RankingTopicRound rankingTopicRound) {
        return rankingTopicRoundJpaRepository.save(rankingTopicRound);
    }

    @Override
    public List<RankingEntry> save(List<RankingEntry> rankingEntries) {
        return rankingEntryJpaRepository.saveAll(rankingEntries);
    }
}
