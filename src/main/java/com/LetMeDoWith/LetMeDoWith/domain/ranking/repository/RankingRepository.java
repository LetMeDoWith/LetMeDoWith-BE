package com.LetMeDoWith.LetMeDoWith.domain.ranking.repository;

<<<<<<< HEAD
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;

=======
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingEntry;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopicRound;
>>>>>>> 016c75020c13bf395fc8ac93665c3efbbed050bb
import java.util.List;
import java.util.Optional;

public interface RankingRepository {
<<<<<<< HEAD

    Optional<RankingTopic> getRankingTopic(RankingTopicCode rankingTopic);

    Optional<RankingTopicRound> getLatestRankingTopicRound(RankingTopic rankingTopic);

    RankingTopicRound save(RankingTopicRound rankingTopicRound);

    void save(List<RankingEntry> rankingEntries);
=======
    Optional<RankingTopic> getRankingTopic(String title, Yn isActive);

    List<RankingEntry> getRankingEntries(Long topicId, Long round);

    RankingTopic save(RankingTopic rankingTopic);

    RankingTopicRound save(RankingTopicRound rankingTopicRound);

    List<RankingEntry> save(List<RankingEntry> rankingEntries);
>>>>>>> 016c75020c13bf395fc8ac93665c3efbbed050bb
}
