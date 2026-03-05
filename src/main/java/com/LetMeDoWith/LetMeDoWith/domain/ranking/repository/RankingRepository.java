package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;

import java.util.List;
import java.util.Optional;

public interface RankingRepository {

    Optional<RankingTopic> getRankingTopic(RankingTopicCode rankingTopic);

    Optional<RankingTopicRound> getLatestRankingTopicRound(RankingTopic rankingTopic);

    RankingTopicRound save(RankingTopicRound rankingTopicRound);

    void save(List<RankingEntry> rankingEntries);
}
