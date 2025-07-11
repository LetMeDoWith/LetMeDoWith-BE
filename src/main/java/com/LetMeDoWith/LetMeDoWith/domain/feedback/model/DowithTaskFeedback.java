package com.LetMeDoWith.LetMeDoWith.domain.feedback.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dowith_task_feedback")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
            String senderMemberId,
            String receiverMemberId,
            Long dowithTaskId,
            Long taskFeedbackTemplateId) {

        return DowithTaskFeedback.builder()
                .senderMemberId(senderMemberId)
                .receiverMemberId(receiverMemberId)
                .dowithTaskId(dowithTaskId)
                .taskFeedbackTemplateId(taskFeedbackTemplateId)
                .isChecked(Yn.FALSE)
                .build();
    }

    /**
     * 잔소리를 추가로 보낼 수 있는지 확인한다. 한 유저는 10분에 한번씩 잔소리를 보낼 수 있다.
     *
     * @param senderId 잔소리를 보내는 유저의 ID
     * @param now 현재 시간
     * @return 잔소리를 추가로 보낼 수 있는지 여부
     */
    public boolean isAdditionalFeedbackAvailable(String senderId, LocalDateTime now) {
        if (!this.senderMemberId.equals(senderId)) {
            return true; // 다른 유저가 보낸 경우, 잔소리를 추가로 보낼 수 있다.
        }

        return this.getCreatedAt() != null && this.getCreatedAt().plusMinutes(10).isBefore(now);
    }

    public DowithTaskFeedback check() {
        this.isChecked = Yn.TRUE;
        return this;
    }
}
