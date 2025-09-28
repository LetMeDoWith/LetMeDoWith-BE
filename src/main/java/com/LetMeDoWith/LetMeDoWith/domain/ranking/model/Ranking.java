package com.LetMeDoWith.LetMeDoWith.domain.ranking.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
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
                    columnNames = {"ranking_topic_id", "member_id"})
        })
public class Ranking extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_topic_id", nullable = false)
    private RankingTopic rankingTopic;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "week", nullable = false)
    private Integer week;

    @Column(name = "member_id", nullable = false, length = 26)
    private String memberId;

    @Column(name = "current_rank", nullable = false)
    private Long currentRank;

    @Column(name = "previous_rank", nullable = true)
    private Long previousRank;
}
