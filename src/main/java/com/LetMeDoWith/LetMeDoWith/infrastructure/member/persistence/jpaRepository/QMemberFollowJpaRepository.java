package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberFollow;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface QMemberFollowJpaRepository {

    Long countFollowingsByFollowerMemberFetchJoinMember(Member follwerMember);

    List<MemberFollow> findAllFollowingsByFollowerMemberFetchJoinMember(Member followerMember, Pageable pageable);

    Long countFollowersByFollowingMemberFetchJoinMember(Member followingMember);

    List<MemberFollow> findAllFollowersByFollowingMemberFetchJoinMember(Member followingMember, Pageable pageable);
}
