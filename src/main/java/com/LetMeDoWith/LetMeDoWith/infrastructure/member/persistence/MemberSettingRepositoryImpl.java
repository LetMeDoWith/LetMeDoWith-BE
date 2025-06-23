package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberAlarmSetting;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberSettingRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberAlarmSettingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberSettingRepositoryImpl implements MemberSettingRepository {
    
    private final MemberAlarmSettingJpaRepository memberAlarmSettingJpaRepository;
    
    @Override
    public MemberAlarmSetting save(MemberAlarmSetting memberAlarmSetting) {
        return memberAlarmSettingJpaRepository.save(memberAlarmSetting);
    }
    
    @Override
    public MemberAlarmSetting findAlarmSettingByMember(Member member) {
        return memberAlarmSettingJpaRepository.findByMember(member);
    }
    
    @Override
    public void delete(MemberAlarmSetting alarmSetting) {
        memberAlarmSettingJpaRepository.delete(alarmSetting);
    }
}