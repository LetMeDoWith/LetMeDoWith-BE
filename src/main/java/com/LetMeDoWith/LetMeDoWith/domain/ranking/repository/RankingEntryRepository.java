package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import java.util.List;

public interface RankingEntryRepository {
    List<RankingEntry> getEntriesByTopicAndRound(Long topicId, Long round);

    List<RankingEntry> save(List<RankingEntry> rankingEntries);
}
