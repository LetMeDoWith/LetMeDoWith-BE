package com.LetMeDoWith.LetMeDoWith.domain.feedback.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.CountryCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_feedback_template_message")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class TaskFeedbackTemplateMessage extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_feedback_template_id", nullable = false)
    private TaskFeedbackTemplate taskFeedbackTemplate;

    @Column(name = "sender_display_message", nullable = false)
    private String senderDisplayMessage;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "language", nullable = false)
    private CountryCode language;
}
