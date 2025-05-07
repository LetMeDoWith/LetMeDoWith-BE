package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberAlarmSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAlarmSettingJpaRepository extends JpaRepository<MemberAlarmSetting, Long> {
    
    MemberAlarmSetting findByMember(Member member);
    
    
}