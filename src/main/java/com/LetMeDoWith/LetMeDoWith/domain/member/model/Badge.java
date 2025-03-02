package com.LetMeDoWith.LetMeDoWith.domain.member.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.BadgeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "BADGE")
public class Badge extends BaseAuditEntity {
    
    @OneToMany(mappedBy = "badge", fetch = FetchType.LAZY)
    protected List<MemberBadge> memberBadges;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dowithTaskId", nullable = false)
    private Long id;
    
    @Column(name = "status", nullable = false)
    private BadgeStatus badgeStatus;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "acquire_hint")
    private String acquireHint;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "sort_order")
    private int sortOrder;

//  public boolean isActive(Long memberId) {
//    if(memberBadges != null && !memberBadges.isEmpty() && BadgeStatus.ACTIVE.equals(badgeStatus)) {
//      return memberBadges.get(0).getMemberId().equals(memberId);
//    }else {
//      return false;
//    }
//  }
//
//  public boolean isMainBadge(Long memberId) {
//    if(memberBadges == null || memberBadges.isEmpty()) return false;
//    MemberBadge memberBadge = this.memberBadges.stream().filter(e -> e.getMemberId().equals(memberId)).findFirst().orElseThrow(() -> new RestApiException(MEMBER_BADGE_NOT_EXIST));
//    return Yn.TRUE.equals(memberBadge.getIsMain());
//  }
//
//  public void registerToMain(Long memberId) {
//    if(memberBadges ==null || memberBadges.isEmpty()) throw new RestApiException(MEMBER_BADGE_NOT_EXIST);
//    MemberBadge memberBadge = this.memberBadges.stream().filter(e -> e.getMemberId().equals(memberId)).findFirst().orElseThrow(() -> new RestApiException(MEMBER_BADGE_NOT_EXIST));
//    memberBadge.registerToMainBadge();
//  }
//
//  public void cancelMain(Long memberId) {
//    if(memberBadges ==null || memberBadges.isEmpty()) throw new RestApiException(MEMBER_BADGE_NOT_EXIST);
//    MemberBadge memberBadge = this.memberBadges.stream().filter(e -> e.getMemberId().equals(memberId)).findFirst().orElseThrow(() -> new RestApiException(MEMBER_BADGE_NOT_EXIST));
//    memberBadge.cancelMainBadge();
//  }
//


}
