package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import java.util.Optional;

public interface QMemberJpaRepository {
    
    
    Optional<Member> findByProviderAndSubject(SocialProvider provider, String subject);
    
    Optional<Member> findByProviderAndSubjectAndStatus(SocialProvider provider,
                                                       String subject,
                                                       MemberStatus status);
    
}