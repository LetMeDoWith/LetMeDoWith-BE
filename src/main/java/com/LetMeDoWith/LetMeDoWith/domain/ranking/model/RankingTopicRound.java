package com.LetMeDoWith.LetMeDoWith.domain.ranking.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "ranking_topic_round",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_ranking_topic_round_1",
                    columnNames = {"ranking_topic_id", "round"})
        })
public class RankingTopicRound extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_topic_id", nullable = false)
    private RankingTopic rankingTopic;

    @Column(name = "round", nullable = false)
    private Long round;

    @Column(name = "aggregation_start_at", nullable = false)
    private LocalDateTime aggregationStartDateTime;

    @Column(name = "aggregation_end_at", nullable = false)
    private LocalDateTime aggregationEndDateTime;

    @OneToMany(mappedBy = "rankingTopicRound", fetch = FetchType.LAZY)
    private List<RankingEntry> rankingEntries;

    public static RankingTopicRound of(
            RankingTopic rankingTopic,
            Long round,
            LocalDateTime aggregationStartDateTime,
            LocalDateTime aggregationEndDateTime) {
        return RankingTopicRound.builder()
                .rankingTopic(rankingTopic)
                .round(round)
                .aggregationStartDateTime(aggregationStartDateTime)
                .aggregationEndDateTime(aggregationEndDateTime)
                .build();
    }

    public static RankingTopicRound nextOf(
            RankingTopic rankingTopic,
            RankingTopicRound previousRound,
            LocalDateTime aggregationStartDateTime,
            LocalDateTime aggregationEndDateTime) {
        Long nextRound = previousRound == null ? 1L : previousRound.getRound() + 1;
        return of(rankingTopic, nextRound, aggregationStartDateTime, aggregationEndDateTime);
    }
}
