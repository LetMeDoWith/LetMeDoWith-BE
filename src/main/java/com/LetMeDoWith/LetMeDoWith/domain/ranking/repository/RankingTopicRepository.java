package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import java.util.Optional;

public interface RankingTopicRepository {
    Optional<RankingTopic> getRankingTopic(String title, Yn isActive);

    RankingTopic save(RankingTopic rankingTopic);
}
