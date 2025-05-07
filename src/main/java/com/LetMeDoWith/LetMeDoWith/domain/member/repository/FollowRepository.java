package com.LetMeDoWith.LetMeDoWith.domain.member.repository;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberFollow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface FollowRepository {
    
    MemberFollow save(Member followerMember, Member followingMember);
    
    List<MemberFollow> getFollowers(Member followingMember, Pageable pageable);
    
    List<MemberFollow> getFollowings(Member followerMember, Pageable pageable);
    
    Optional<MemberFollow> getFollowing(String memberId, String followingMemberId);
    
    void delete(MemberFollow memberFollow);
    
}