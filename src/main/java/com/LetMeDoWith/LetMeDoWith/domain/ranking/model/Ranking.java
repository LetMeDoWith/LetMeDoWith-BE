package com.LetMeDoWith.LetMeDoWith.domain.ranking.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
        name = "ranking",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_ranking_1",
                    columnNames = {"ranking_topic_id", "entity_id"})
        })
public class Ranking extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_topic_id", nullable = false)
    private RankingTopic rankingTopic;

    @Column(name = "year", nullable = true)
    private Integer year;

    @Column(name = "month", nullable = true)
    private Integer month;

    @Column(name = "week", nullable = true)
    private Integer week;

    @Column(name = "entity_id", nullable = false, length = 26)
    private String entityId;

    @Column(name = "current_rank", nullable = false)
    private Long currentRank;

    @Column(name = "previous_rank", nullable = true)
    private Long previousRank;
}
