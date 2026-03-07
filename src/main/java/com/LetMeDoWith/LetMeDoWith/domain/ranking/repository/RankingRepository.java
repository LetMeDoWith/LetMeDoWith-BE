package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
import java.util.List;
import java.util.Optional;

public interface RankingRepository {
    Optional<RankingTopic> getRankingTopic(String title, Yn isActive);

    List<RankingEntry> getRankingEntries(Long topicId, Long round);

    RankingTopic save(RankingTopic rankingTopic);

    RankingTopicRound save(RankingTopicRound rankingTopicRound);

    List<RankingEntry> save(List<RankingEntry> rankingEntries);
}
