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
        name = "ranking_entry",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_ranking_entry_1",
                    columnNames = {"ranking_topic_round_id", "member_id"})
        })
public class RankingEntry extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_topic_round_id", nullable = false)
    private RankingTopicRound rankingTopicRound;

    @Column(name = "member_id", nullable = false, length = 26)
    private String memberId;

    @Column(name = "current_rank", nullable = false)
    private Long currentRank;

    @Column(name = "previous_rank", nullable = true)
    private Long previousRank;

    public static RankingEntry of(
            RankingTopicRound rankingTopicRound, String memberId, Long currentRank, Long previousRank) {
        return RankingEntry.builder()
                .rankingTopicRound(rankingTopicRound)
                .memberId(memberId)
                .currentRank(currentRank)
                .previousRank(previousRank)
                .build();
    }

    public void updateRank(Long currentRank, Long previousRank) {
        this.currentRank = currentRank;
        this.previousRank = previousRank;
    }
}
