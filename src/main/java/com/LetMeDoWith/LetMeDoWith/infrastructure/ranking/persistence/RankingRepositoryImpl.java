package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
<<<<<<< HEAD
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
=======
>>>>>>> 016c75020c13bf395fc8ac93665c3efbbed050bb
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.repository.RankingRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingEntryJpaRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository.RankingTopicJpaRepository;
<<<<<<< HEAD
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
=======
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
>>>>>>> 016c75020c13bf395fc8ac93665c3efbbed050bb
    }

    @Override
    public RankingTopicRound save(RankingTopicRound rankingTopicRound) {
<<<<<<< HEAD
        return rankingTopicRoundRepository.save(rankingTopicRound);
    }

    @Override
    public void save(List<RankingEntry> rankingEntries) {
        rankingEntryJpaRepository.saveAll(rankingEntries);
=======
        return rankingTopicRoundJpaRepository.save(rankingTopicRound);
    }

    @Override
    public List<RankingEntry> save(List<RankingEntry> rankingEntries) {
        return rankingEntryJpaRepository.saveAll(rankingEntries);
>>>>>>> 016c75020c13bf395fc8ac93665c3efbbed050bb
    }
}
