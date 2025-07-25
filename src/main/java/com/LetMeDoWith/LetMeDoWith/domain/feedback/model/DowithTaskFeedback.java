package com.LetMeDoWith.LetMeDoWith.domain.feedback.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.AggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dowith_task_feedback")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AggregateRoot
public class DowithTaskFeedback extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "task_feedback_template_id", nullable = false)
    private Long taskFeedbackTemplateId;

    @Column(name = "dowith_task_id", nullable = false)
    private Long dowithTaskId;

    @Column(name = "sender_member_id", nullable = false)
    private String senderMemberId;

    @Column(name = "receiver_member_id", nullable = false)
    private String receiverMemberId;

    @Column(name = "is_checked", nullable = false)
    private Yn isChecked;

    public static DowithTaskFeedback of(
            String senderMemberId, String receiverMemberId, Long dowithTaskId, Long taskFeedbackTemplateId) {

        return DowithTaskFeedback.builder()
                .senderMemberId(senderMemberId)
                .receiverMemberId(receiverMemberId)
                .dowithTaskId(dowithTaskId)
                .taskFeedbackTemplateId(taskFeedbackTemplateId)
                .isChecked(Yn.FALSE)
                .build();
    }

    /** 피드백을 확인 상태로 변경한다. */
    public void check() {
        this.isChecked = Yn.TRUE;
    }
}
