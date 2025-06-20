package com.LetMeDoWith.LetMeDoWith.domain.feedback.model;

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

@Entity
@Table(name = "task_feedback_template_message")
public class TaskFeedbackTemplateMessage extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_feedback_template_id", nullable = false)
    private TaskFeedbackTemplate taskFeedbackTemplate;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @Column(name = "language", nullable = false)
    private String language;
}