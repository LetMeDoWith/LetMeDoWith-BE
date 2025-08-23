package com.LetMeDoWith.LetMeDoWith.domain.task.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.domain.AggregateRoot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "DOWITH_TASK_SUCCESS")
@AggregateRoot
public class DowithTaskSuccess extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dowith_task_id", nullable = false)
    private DowithTask dowithTask;

    @Column(name = "image_url")
    private String imageUrl;

    public static DowithTaskSuccess of(DowithTask dowithTask, String imageUrl) {
        return DowithTaskSuccess.builder()
                .dowithTask(dowithTask)
                .imageUrl(imageUrl)
                .build();
    }
}
