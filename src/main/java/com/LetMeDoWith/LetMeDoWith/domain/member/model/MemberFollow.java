package com.LetMeDoWith.LetMeDoWith.domain.member.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "member_follow")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFollow extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private Member followerMember;
    
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private Member followingMember;
}