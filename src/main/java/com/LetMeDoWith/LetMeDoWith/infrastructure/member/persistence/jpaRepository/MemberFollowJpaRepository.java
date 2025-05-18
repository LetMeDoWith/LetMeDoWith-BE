package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberFollowJpaRepository
        extends JpaRepository<MemberFollow, Long>, QMemberFollowJpaRepository {

    Optional<MemberFollow> findByFollowerMemberIdAndFollowingMemberId(
            String followerMemberId, String followingMemberId);
}
