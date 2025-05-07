package com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Badge;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberBadge;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberBadgeJpaRepository extends JpaRepository<MemberBadge, Long> {
    
    Optional<MemberBadge> findByMemberIdAndIsMain(String memberId, Yn isMain);
    
    Optional<MemberBadge> findByMemberIdAndBadge(String memberId, Badge badge);
}