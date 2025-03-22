package com.LetMeDoWith.LetMeDoWith.domain.member.model;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberAlarmSettingVO;
import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "member_alarm_setting")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberAlarmSetting extends BaseAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean baseAlarmYn = true;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean todoBotYn = true;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean feedbackYn = true;
    
    @Column(nullable = false)
    @Builder.Default
    private boolean marketingYn = true;
    
    public static MemberAlarmSetting init(Member member) {
        
        MemberAlarmSetting alarmSetting = MemberAlarmSetting.builder()
                                                            .member(member)
                                                            .baseAlarmYn(true)
                                                            .todoBotYn(true)
                                                            .feedbackYn(true)
                                                            .marketingYn(true)
                                                            .build();
        
        member.linkAlarmSetting(alarmSetting);
        
        return alarmSetting;
    }
    
    public MemberAlarmSetting update(MemberAlarmSettingVO settingVO) {
        this.baseAlarmYn = settingVO.baseAlarmYn();
        this.todoBotYn = settingVO.todoBotYn();
        this.feedbackYn = settingVO.feedbackYn();
        this.marketingYn = settingVO.marketingYn();
        
        return this;
    }
}