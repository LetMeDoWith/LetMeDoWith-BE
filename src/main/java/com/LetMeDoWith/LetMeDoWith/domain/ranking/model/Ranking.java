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
@Table(name = "ranking")
public class Ranking extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "ranking_topic_id", nullable = false)
    private RankingTopic rankingTopic;

    @Column(name = "member_id", nullable = false, length = 26)
    private String memberId;

    @Column(name = "rank", nullable = false)
    private Long rank;

    @Column(name = "previous_rank", nullable = true)
    private Long previousRank;

}
