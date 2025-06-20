package com.LetMeDoWith.LetMeDoWith.domain.feedback.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dowith_task_feedback")
public class DowithTaskFeedback extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "task_feedback_template_id", nullable = false)
    private Long taskFeedbackTemplateId;
    
    @Column(name = "dowith_task_id", nullable = false)
    private Long dowithTaskId;
    
    @Column(name = "sender_id", nullable = false)
    private String senderId;
    
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;
    
    @Column(name = "is_checked", nullable = false)
    private Yn isChecked;
}