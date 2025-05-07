package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberFollow;

public interface QMemberFollowJpaRepository {
    
    List<MemberFollow> findAllFollowingsByFollowerMemberFetchJoinMember(Member followerMember,
                                                                        Pageable pageable);
    
    List<MemberFollow> findAllFollowersByFollowingMemberFetchJoinMember(Member followingMember,
                                                                        Pageable pageable);
}
