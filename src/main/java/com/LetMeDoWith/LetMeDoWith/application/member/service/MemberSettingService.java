package com.LetMeDoWith.LetMeDoWith.application.member.service;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.UpdateMemberAlarmSettingCommand;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberAlarmSetting;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberSettingRepository;
import com.LetMeDoWith.LetMeDoWith.domain.member.vo.MemberAlarmSettingVO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberSettingService {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;

    /**
     * 유저의 알람 수신 상태를 변경한다.
     *
     * @param command 변경하려는 알람 수신 상태
     */
    @Transactional
    public void updateAlarmSetting(UpdateMemberAlarmSettingCommand command) {
        Member member = memberRepository
                .getMember(AuthUtil.getMemberId(), MemberStatus.NORMAL)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.MEMBER_NOT_EXIST));

        MemberAlarmSetting alarmSetting = member.getAlarmSetting();

        memberSettingRepository.save(alarmSetting.update(MemberAlarmSettingVO.builder()
                .baseAlarmYn(command.baseAlarmYn())
                .todoBotYn(command.todoBotYn())
                .feedbackYn(command.feedbackYn())
                .marketingYn(command.marketingYn())
                .build()));
    }
}
