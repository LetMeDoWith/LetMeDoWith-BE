package com.LetMeDoWith.LetMeDoWith.infrastructure.ranking.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
<<<<<<< HEAD
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankingTopicJpaRepository extends JpaRepository<RankingTopic, Long> {
    Optional<RankingTopic> findByCodeAndIsActive(RankingTopicCode code, Yn isActive);
=======
import com.LetMeDoWith.LetMeDoWith.domain.ranking.model.RankingTopic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankingTopicJpaRepository extends JpaRepository<RankingTopic, Long> {
    Optional<RankingTopic> findByTitleAndIsActive(String title, Yn isActive);
>>>>>>> 016c75020c13bf395fc8ac93665c3efbbed050bb
}
