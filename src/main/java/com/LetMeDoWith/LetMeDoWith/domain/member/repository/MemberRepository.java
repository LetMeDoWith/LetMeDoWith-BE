package com.LetMeDoWith.LetMeDoWith.domain.member.repository;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberTermAgree;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    
    Member save(Member member);
    
    MemberTermAgree save(MemberTermAgree memberTermAgree);
    
    void saveSocialAccount(MemberSocialAccount memberSocialAccount);
    
    Optional<Member> getMember(Long id, MemberStatus memberStatus);
    
    Optional<Member> getNormalStatusMember(Long id);
    
    List<Member> getMembers(String nickname, List<MemberStatus> memberStatuses);
    
    Optional<Member> getMember(SocialProvider provider, String subject);
    
}

