package com.LetMeDoWith.LetMeDoWith.entity.member;

import com.LetMeDoWith.LetMeDoWith.enums.converter.member.MemberStatusConverter;
import com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "member_status_history")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberStatusHistory extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_status_history_id", nullable = false)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Convert(converter = MemberStatusConverter.class)
    @Column(nullable = false)
    private MemberStatus status;
    
    @Column(nullable = false)
    private LocalDateTime statusChangedAt;
    
    @Column(nullable = false)
    private LocalDateTime statusEndAt;
    
}