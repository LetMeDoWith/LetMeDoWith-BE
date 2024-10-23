package com.LetMeDoWith.LetMeDoWith.application.Member;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberAlarmSettingVO;
import com.LetMeDoWith.LetMeDoWith.application.member.dto.UpdateMemberAlarmSettingCommand;
import com.LetMeDoWith.LetMeDoWith.application.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.application.member.repository.MemberSettingRepository;
import com.LetMeDoWith.LetMeDoWith.application.member.service.MemberSettingService;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.domain.member.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.MemberAlarmSetting;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberSettingServiceTest {
    
    @Mock
    private MemberSettingRepository memberSettingRepository;
    
    @Mock
    private MemberRepository memberRepository;
    
    @InjectMocks
    private MemberSettingService memberSettingService;
    
    @Test
    @DisplayName("[SUCCESS] 알람 수신 여부 변경")
    void changeAlarmReceiveStatusTest() {
        
        // given
        Member testMemberObj = Member.builder()
                                     .id(1L)
                                     .subject("test")
                                     .nickname("nickname")
                                     .selfDescription("self desc")
                                     .status(MemberStatus.NORMAL)
                                     .type(MemberType.USER)
                                     .profileImageUrl("image.jpeg")
                                     .build();
        
        MemberAlarmSetting alarmSetting = MemberAlarmSetting.init(testMemberObj);
        
        MockedStatic<AuthUtil> mockedAuthUtil = mockStatic(AuthUtil.class);
        mockedAuthUtil.when(AuthUtil::getMemberId).thenReturn(1L);
        
        when(memberRepository.getMember(eq(1L),
                                        eq(MemberStatus.NORMAL)))
            .thenReturn(Optional.of(testMemberObj));
        
        UpdateMemberAlarmSettingCommand tobeCommand =
            UpdateMemberAlarmSettingCommand.builder()
                                           .baseAlarmYn(false)
                                           .todoBotYn(false)
                                           .feedbackYn(false)
                                           .marketingYn(false)
                                           .build();
        
        // when
        memberSettingService.updateAlarmSetting(tobeCommand);
        
        // then
        MemberAlarmSetting tobeAlarmSetting = testMemberObj.getAlarmSetting();
        
        assertFalse(tobeAlarmSetting.isBaseAlarmYn());
        assertFalse(tobeAlarmSetting.isTodoBotYn());
        assertFalse(tobeAlarmSetting.isFeedbackYn());
        assertFalse(tobeAlarmSetting.isMarketingYn());
        
        verify(memberSettingRepository).save(
            alarmSetting.update(MemberAlarmSettingVO.fromCommand(tobeCommand))
        );
        
        mockedAuthUtil.close();
    }
    
    @Test
    @DisplayName("[FAIL] 존재하지 않는 유저로 시도")
    void changeAlarmSettingStatusWithNotExistingMember() {
        // given
        MockedStatic<AuthUtil> mockedAuthUtil = mockStatic(AuthUtil.class);
        mockedAuthUtil.when(AuthUtil::getMemberId).thenReturn(1L);
        
        when(memberRepository.getMember(eq(1L),
                                        eq(MemberStatus.NORMAL)))
            .thenReturn(Optional.empty());
        
        UpdateMemberAlarmSettingCommand tobeCommand =
            UpdateMemberAlarmSettingCommand.builder()
                                           .baseAlarmYn(false)
                                           .todoBotYn(false)
                                           .feedbackYn(false)
                                           .marketingYn(false)
                                           .build();
        
        // then
        Assertions.assertThrows(RestApiException.class,
                                () -> memberSettingService.updateAlarmSetting(tobeCommand),
                                FailResponseStatus.MEMBER_NOT_EXIST.getMessage());
        
        mockedAuthUtil.close();
    }
    
}