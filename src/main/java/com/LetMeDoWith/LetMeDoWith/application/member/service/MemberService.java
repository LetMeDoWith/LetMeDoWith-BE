package com.LetMeDoWith.LetMeDoWith.application.member.service;


import com.LetMeDoWith.LetMeDoWith.application.member.dto.CreateSignupCompletedMemberCommand;
import com.LetMeDoWith.LetMeDoWith.application.member.dto.MemberPersonalInfoVO;
import com.LetMeDoWith.LetMeDoWith.application.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.application.member.repository.MemberSettingRepository;
import com.LetMeDoWith.LetMeDoWith.common.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.AuthUtil;
import com.LetMeDoWith.LetMeDoWith.domain.member.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.domain.member.MemberTermAgree;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;
    
    /**
     * (Provider, Subject) 의 조합으로 기 가입된 계정이 존재하는지 확인한다.
     *
     * @param provider
     * @param subject
     * @return 기 가입된 계정. Optional 타입을 리턴한다..
     */
    public Optional<Member> getRegisteredMember(SocialProvider provider, String subject) {
        
        return memberRepository.getMember(provider, subject);
        
    }
    
    /**
     * 소셜 인증 완료 후 임시 멤버를 생성한다.
     *
     * @param provider 소셜 인증 제공자
     * @param subject  provider별 유저 고유번호
     * @return 임시 멤버 객체
     */
    @Transactional
    public Member createSocialAuthenticatedMember(SocialProvider provider, String subject) {
        
        Member socialAuthenticatedMember = memberRepository.save(Member.socialAuthenticated(subject));
        memberRepository.saveSocialAccount(MemberSocialAccount.of(socialAuthenticatedMember,
                                                                  provider));
        
        return socialAuthenticatedMember;
    }
    
    /**
     * 회원가입 완료 요청을 처리하여 Member 정보를 업데이트한다.
     *
     * @param command 회원가입 완료 요청
     * @return 업데이트된 멤버 객체
     * @throws RestApiException 유효하지 않은 토큰이거나, memberId가 유효하지 않은 경우
     */
    @Transactional
    public Member createSignupCompletedMember(CreateSignupCompletedMemberCommand command) {
        
        Member member = memberRepository.getMember(AuthUtil.getMemberId(),
                                                   MemberStatus.SOCIAL_AUTHENTICATED)
                                        .orElseThrow(() -> new RestApiException(FailResponseStatus.MEMBER_NOT_EXIST));
        
        if (isExistingNickname(command.nickname())) {
            throw new RestApiException(FailResponseStatus.DUPLICATE_NICKNAME);
        }
        
        member.updatePersonalInfoWithCompletingSignUp(MemberPersonalInfoVO.builder()
                                                                          .nickname(command.nickname())
                                                                          .dateOfBirth(command.dateOfBirth())
                                                                          .gender(command.gender())
                                                                          .build());
        
        memberRepository.save(MemberTermAgree.of(member,
                                                 command.isTerms(),
                                                 command.isPrivacy(),
                                                 command.isAdvertisement()));
        
        return memberRepository.save(member);
    }
    
    /**
     * 회원의 약관 동의 정보를 생성한다.
     *
     * @param isTermsAgree
     * @param isPrivacyAgree
     * @param isAdvertisementAgree
     * @throws RestApiException 필수 동의 항목이 false이거나, 회원이 존재하지 않을 경우
     */
    @Deprecated
    @Transactional
    public void createMemberTermAgree(boolean isTermsAgree, boolean isPrivacyAgree,
                                      boolean isAdvertisementAgree) {
        
        Member member = memberRepository.getMember(AuthUtil.getMemberId(),
                                                   MemberStatus.SOCIAL_AUTHENTICATED)
                                        .orElseThrow(() -> new RestApiException(FailResponseStatus.MEMBER_NOT_EXIST));

//        memberRepository.saveAgreement(member, MemberAgreementCommand.builder()
//                                                                     .isTermsAgree(isTermsAgree)
//                                                                     .isPrivacyAgree(isPrivacyAgree)
//                                                                     .isAdvertisementAgree(
//                                                                         isAdvertisementAgree)
//                                                                     .build());
    
    }
    
    /**
     * 닉네임의 중복 여부를 확인한다.
     *
     * @param nickname 중복여부를 확인하려는 닉네임
     * @return 닉네임의 중복 여부
     */
    public boolean isExistingNickname(String nickname) {
        
        if (nickname.trim().isEmpty()) {
            throw new RestApiException(FailResponseStatus.MANDATORY_PARAM_ERROR_NAME);
        }
        
        return !memberRepository.getMembers(nickname, Member.getAllMemberStatus()).isEmpty();
        
    }
    
    
    /**
     * 멤버를 탈퇴처리한다.
     * 실제 데이터베이스에서 멤버는 삭제되지 않고, 탈퇴 상태로 변경된다.
     *
     * @param memberId 탈퇴하려는 멤버의 id
     * @return 탈퇴요청 성공 여부
     */
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.getMember(memberId, MemberStatus.NORMAL)
                                        .orElseThrow(() -> new RestApiException(FailResponseStatus.MEMBER_NOT_EXIST));
        
        memberRepository.save(member.withdraw());
    }
}