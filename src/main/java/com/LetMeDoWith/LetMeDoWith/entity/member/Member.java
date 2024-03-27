package com.LetMeDoWith.LetMeDoWith.entity.member;

import com.LetMeDoWith.LetMeDoWith.enums.converter.member.MemberStatusConverter;
import com.LetMeDoWith.LetMeDoWith.enums.converter.member.MemberTypeConverter;
import com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;
    
    @Column
    private String email;
    
    @Convert(converter = MemberStatusConverter.class)
    @Column(nullable = false)
    private MemberStatus status;
    
    @Column(nullable = false)
    private String nickname;
    
    @Column(name = "self_description")
    private String selfDescription;
    
    @Convert(converter = MemberTypeConverter.class)
    @Column(nullable = false)
    private MemberType type;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "marketing_term_agree_yn", nullable = false)
    private boolean marketingTermAgreeYn = false;
    
}