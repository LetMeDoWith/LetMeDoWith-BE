package com.LetMeDoWith.LetMeDoWith.service.Member;


import com.LetMeDoWith.LetMeDoWith.dto.requestDto.CreateMemberTermAgreeReq;
import com.LetMeDoWith.LetMeDoWith.dto.requestDto.SignupCompleteReq;
import com.LetMeDoWith.LetMeDoWith.entity.member.Member;
import com.LetMeDoWith.LetMeDoWith.entity.member.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.entity.member.MemberTermAgree;
import com.LetMeDoWith.LetMeDoWith.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.enums.common.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.enums.member.MemberType;
import com.LetMeDoWith.LetMeDoWith.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.provider.AuthTokenProvider;
import com.LetMeDoWith.LetMeDoWith.provider.AuthTokenProvider.TokenType;
import com.LetMeDoWith.LetMeDoWith.repository.member.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.repository.member.MemberSocialAccountRepository;
import com.LetMeDoWith.LetMeDoWith.repository.member.MemberTermAgreeRepository;
import com.LetMeDoWith.LetMeDoWith.util.AuthUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final MemberSocialAccountRepository memberSocialAccountRepository;
    private final MemberTermAgreeRepository memberTermAgreeRepository;
    private final AuthTokenProvider authTokenProvider;
    
    /**
     * (Provider, Subject) 의 조합으로 기 가입된 계정이 존재하는지 확인한다.
     *
     * @param provider
     * @param subject
     * @return 기 가입된 계정. Optional 타입을 리턴한다..
     */
    public Optional<Member> getRegisteredMember(SocialProvider provider, String subject) {
        return memberRepository.findByProviderAndSubject(provider, subject);
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
        Member temporalMember = memberRepository.save(Member.builder()
                                                            .subject(subject)
                                                            .type(MemberType.USER)
                                                            .status(MemberStatus.SOCIAL_AUTHENTICATED)
                                                            .build());
        
        MemberSocialAccount socialAccount = MemberSocialAccount.builder()
                                                               .member(temporalMember)
                                                               .provider(provider)
                                                               .build();
        // 양방향 연관관계 매핑
        temporalMember.getSocialAccountList().add(socialAccount);
        memberSocialAccountRepository.save(socialAccount);
        
        return temporalMember;
    }
    
    /**
     * 회원가입 완료 요청을 처리하여 Member 정보를 업데이트한다.
     *
     * @param signupCompleteReq 회원가입을 완료하려는 회원의 나머지 추가 정보
     * @return 업데이트된 멤버 객체
     * @throws RestApiException 유효하지 않은 토큰이거나, memberId가 유효하지 않은 경우
     */
    @Transactional
    public Member createSignupCompletedMember(SignupCompleteReq signupCompleteReq) {
        Long memberId = authTokenProvider.validateToken(signupCompleteReq.signupToken(),
                                                        TokenType.SIGNUP);
        
        return memberRepository.findById(memberId).map(member -> {
            if (isExistingNickname(signupCompleteReq.nickname())) {
                throw new RestApiException(FailResponseStatus.DUPLICATE_NICKNAME);
            }
            
            member.setNickname(signupCompleteReq.nickname());
            member.setDateOfBirth(signupCompleteReq.dateOfBirth());
            member.setGender(signupCompleteReq.gender());
            
            member.setStatus(MemberStatus.NORMAL);
            
            // 수정된 멤버를 저장하고 반환
            return memberRepository.save(member);
        }).orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_TOKEN));
    }
    
    /**
     * 회원의 약관 동의 정보를 생성한다.
     *
     * @param createMemberTermAgreeReq 회원의 약관 동의 정보를 담은 요청 객체
     * @throws RestApiException 필수 동의 항목이 false이거나, 회원이 존재하지 않을 경우
     */
    @Transactional
    public void createMemberTermAgree(CreateMemberTermAgreeReq createMemberTermAgreeReq) {
        Long memberId = AuthUtil.getMemberId();
        
        memberRepository.findById(memberId).ifPresentOrElse(member -> {
            if (!createMemberTermAgreeReq.termsOfAgree() || !createMemberTermAgreeReq.privacy()) {
                throw new RestApiException(FailResponseStatus.INVALID_PARAM_ERROR);
            }
            
            MemberTermAgree memberTermAgree = MemberTermAgree.builder()
                                                             .advertisement(createMemberTermAgreeReq.advertisement())
                                                             .member(member)
                                                             .build();
            
            member.setTermAgree(memberTermAgree);
            memberTermAgreeRepository.save(memberTermAgree);
            memberRepository.save(member);
            
        }, () -> {
            throw new RestApiException(FailResponseStatus.MEMBER_NOT_EXIST);
        });
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
        
        return memberRepository.findByNickname(nickname.trim()).isPresent();
    }
}