package com.LetMeDoWith.LetMeDoWith.domain.member;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberPersonalInfoVO;
import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.Gender;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.TaskCompleteLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class Member extends BaseAuditEntity {
    
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    List<MemberSocialAccount> socialAccountList = new ArrayList<>();
    
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    List<MemberStatusHistory> statusHistoryList = new ArrayList<>();
    
    @OneToMany(mappedBy = "followerMember", fetch = FetchType.LAZY)
    List<MemberFollow> followingMembers = new ArrayList<>();
    
    @OneToMany(mappedBy = "followingMember", fetch = FetchType.LAZY)
    List<MemberFollow> followerMembers = new ArrayList<>();
    
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    MemberTermAgree termAgree;
    
    @OneToOne(mappedBy = "member")
    MemberAlarmSetting alarmSetting;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    
    // OpenID Connect id token의 sub 필드와 동일
    // (sub, provider) 조합으로 uniqueness 판단함.
    @Column
    private String subject;
    
    @Column(nullable = false)
    private MemberStatus status;
    
    @Column(name = "task_complete_level")
    private TaskCompleteLevel taskCompleteLevel;
    
    @Column
    private String nickname;
    
    @Column(name = "self_description")
    private String selfDescription;
    
    @Column(columnDefinition = "VARCHAR(2)")
    private Gender gender;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(nullable = false)
    private MemberType type;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    /**
     * 소셜 로그인이 완료된 직후 상태(초기 상태)의 Member를 생성한다.
     *
     * @param subject OIDC id token에서 추출한 회원 구분 번호.
     * @return 초기회된 멤버 객체
     */
    public static Member socialAuthenticated(String subject) {
        return Member.builder()
                     .subject(subject)
                     .type(MemberType.USER)
                     .status(MemberStatus.SOCIAL_AUTHENTICATED)
                     .taskCompleteLevel(TaskCompleteLevel.GOOD)
                     .build();
    }
    
    public static List<MemberStatus> getAllMemberStatus() {
        return List.of(MemberStatus.NORMAL,
                       MemberStatus.SUSPENDED,
                       MemberStatus.WITHDRAWN,
                       MemberStatus.SOCIAL_AUTHENTICATED);
    }
    
    public static List<MemberStatus> getActiveMemberStatus() {
        return List.of(MemberStatus.NORMAL);
    }
    
    public static List<MemberStatus> getInactiveMemberStatus() {
        return List.of(MemberStatus.SUSPENDED,
                       MemberStatus.WITHDRAWN,
                       MemberStatus.SOCIAL_AUTHENTICATED);
    }
    
    // LAZY Badge 획득 레벨인지 확인
    public boolean isLazyBadgeAcquireLevel() {
        return TaskCompleteLevel.BAD.equals(this.taskCompleteLevel);
    }
    
    /**
     * Member의 개인정보를 업데이트 한다.
     *
     * @param personalInfo 멤버의 개인 정보. 닉테임, 프로필 사진, 상태 메세지, 생년월일. 필요시 성별
     * @return 싱태가 업데이트 된 멤버 객체
     */
    public Member updatePersonalInfo(MemberPersonalInfoVO personalInfo) {
        if (personalInfo != null) {
            if (!personalInfo.nickname().trim().isEmpty()) {
                this.nickname = personalInfo.nickname();
            }
            
            if (personalInfo.profileImageUrl() != null &&
                !personalInfo.profileImageUrl().isEmpty()) {
                this.profileImageUrl = personalInfo.profileImageUrl();
            }
            
            if (personalInfo.selfDescription() != null &&
                !personalInfo.selfDescription().isEmpty()) {
                this.selfDescription = personalInfo.selfDescription();
            }
            
            if (personalInfo.dateOfBirth() != null) {
                this.dateOfBirth = personalInfo.dateOfBirth();
            }
            
            this.gender = personalInfo.gender();
        }
        
        return this;
    }
    
    /**
     * Member의 개인정보를 업데이트 하고, 회원 가입 완료 상태로 변경한다.
     *
     * @param personalInfoVO 회원의 개인 정보.
     * @return 개인정보가 입력되고 회원가입 완료 상태로 변경된 회원 객체
     */
    public Member updatePersonalInfoWithCompletingSignUp(MemberPersonalInfoVO personalInfoVO) {
        return this.updatePersonalInfo(personalInfoVO)
                   .changeStatusTo(MemberStatus.NORMAL);
    }
    
    public Member withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        
        return this;
    }
    
    private Member changeStatusTo(MemberStatus status) {
        this.status = status;
        
        return this;
    }
    
    private Member changeTaskCompleteLevelTo(TaskCompleteLevel taskCompleteLevel) {
        this.taskCompleteLevel = taskCompleteLevel;
        
        return this;
    }
    
    public Member linkTermAgree(MemberTermAgree termAgree) {
        this.termAgree = termAgree;
        
        return this;
    }
    
    public Member addSocialAccount(MemberSocialAccount memberSocialAccount) {
        this.socialAccountList.add(memberSocialAccount);
        
        return this;
    }
    
    public Member linkAlarmSetting(MemberAlarmSetting alarmSetting) {
        this.alarmSetting = alarmSetting;
        
        return this;
    }
    
}