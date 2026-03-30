package com.LetMeDoWith.LetMeDoWith.domain.ranking.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.ranking.RankingTopicCode;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
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

    public static RankingTopic ofActive(RankingTopicCode code, String title, String description) {
        return RankingTopic.builder()
                .code(code)
                .title(title)
                .description(description)
                .isActive(Yn.TRUE)
                .build();
    }

    public void updateCurrentRound(RankingTopicRound currentRound) {
        this.currentRound = currentRound;
    }
}
