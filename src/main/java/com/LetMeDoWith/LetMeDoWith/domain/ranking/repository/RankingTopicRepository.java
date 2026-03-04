package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import java.util.Optional;

public interface RankingTopicRepository {
    Optional<RankingTopic> getActiveTopicByTitle(String title);

    RankingTopic save(RankingTopic rankingTopic);
}
