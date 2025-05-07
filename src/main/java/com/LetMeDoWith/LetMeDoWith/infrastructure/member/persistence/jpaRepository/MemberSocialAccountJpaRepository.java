package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberSocialAccount;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSocialAccountJpaRepository extends JpaRepository<MemberSocialAccount, Long> {
    
    Optional<MemberSocialAccount> findByMemberAndProvider(Member member, SocialProvider provider);
}