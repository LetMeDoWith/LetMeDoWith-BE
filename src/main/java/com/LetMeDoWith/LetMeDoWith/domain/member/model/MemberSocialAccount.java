package com.LetMeDoWith.LetMeDoWith.domain.member.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
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

@Entity(name = "member_social_account")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSocialAccount extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(name = "provider", nullable = false)
    private SocialProvider provider;
    
    public static MemberSocialAccount of(Member member, SocialProvider provider) {
        MemberSocialAccount socialAccount = MemberSocialAccount.builder()
                                                               .member(member)
                                                               .provider(provider)
                                                               .build();
        
        member.addSocialAccount(socialAccount);
        return socialAccount;
    }
    
}