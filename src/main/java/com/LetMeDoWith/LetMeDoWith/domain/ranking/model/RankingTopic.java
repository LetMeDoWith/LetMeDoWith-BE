package com.LetMeDoWith.LetMeDoWith.domain.ranking.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Column(name = "code", nullable = false, length = 255)
    private RankingTopicCode code;

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
}
