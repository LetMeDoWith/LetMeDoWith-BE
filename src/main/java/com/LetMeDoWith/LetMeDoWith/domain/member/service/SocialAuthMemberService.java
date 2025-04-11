package com.LetMeDoWith.LetMeDoWith.domain.member.service;

import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

/**
 * 회원 소셜 인증 Domain Service
 */
@Service
public class SocialAuthMemberService {
    
    /**
     * 소셜 인증 완료 후 임시 멤버를 생성한다.
     *
     * @param provider 소셜 인증 제공자
     * @param subject  provider별 유저 고유번호
     * @return 임시 멤버 객체
     */
    public Member createSocialAuthenticatedMember(SocialProvider provider, String subject,
                                                  MemberRepository memberRepository) {
        Member socialAuthenticatedMember = memberRepository.save(Member.ofSocialAuthenticated(
            subject));
        memberRepository.saveSocialAccount(MemberSocialAccount.of(socialAuthenticatedMember,
                                                                  provider));
        return socialAuthenticatedMember;
    }
    
}
