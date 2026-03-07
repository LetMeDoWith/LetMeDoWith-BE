package com.LetMeDoWith.LetMeDoWith.domain.ranking.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import jakarta.persistence.*;
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
@Table(name = "ranking_topic")
public class RankingTopic extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "active_yn", nullable = false)
    @Builder.Default
    private Yn isActive = Yn.TRUE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_round_id", nullable = true)
    private RankingTopicRound currentRound;

    @OneToMany(mappedBy = "rankingTopic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RankingTopicRound> rankingTopicRounds;

    public void updateCurrentRound(RankingTopicRound currentRound) {
        this.currentRound = currentRound;
    }
}
