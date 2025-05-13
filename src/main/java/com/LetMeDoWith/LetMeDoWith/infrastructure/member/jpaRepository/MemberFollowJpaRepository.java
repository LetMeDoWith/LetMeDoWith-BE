package com.LetMeDoWith.LetMeDoWith.infrastructure.member.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberFollow;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberFollowJpaRepository
        extends JpaRepository<MemberFollow, Long>, QMemberFollowJpaRepository {

    Optional<MemberFollow> findByFollowerMemberIdAndFollowingMemberId(
            String followerMemberId, String followingMemberId);
}
