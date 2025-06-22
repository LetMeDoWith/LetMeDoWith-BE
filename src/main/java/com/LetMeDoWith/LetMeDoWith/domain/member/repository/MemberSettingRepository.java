package com.LetMeDoWith.LetMeDoWith.domain.member.repository;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberAlarmSetting;

public interface MemberSettingRepository {
    
    MemberAlarmSetting save(MemberAlarmSetting memberAlarmSetting);
    
    MemberAlarmSetting findAlarmSettingByMember(Member member);
    
    void delete(MemberAlarmSetting alarmSetting);
}