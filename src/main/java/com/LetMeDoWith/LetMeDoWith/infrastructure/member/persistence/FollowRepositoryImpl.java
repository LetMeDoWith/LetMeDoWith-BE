package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.MemberFollow;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.FollowRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository.MemberFollowJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {

    private final MemberFollowJpaRepository memberFollowJpaRepository;

    @Override
    public MemberFollow save(Member followerMember, Member followingMember) {

        return memberFollowJpaRepository.save(
                MemberFollow.builder()
                        .followerMember(followerMember)
                        .followingMember(followingMember)
                        .build());
    }

    @Override
    public List<MemberFollow> getFollowers(Member followingMember, Pageable pageable) {

        return memberFollowJpaRepository.findAllFollowersByFollowingMemberFetchJoinMember(
                followingMember, pageable);
    }

    @Override
    public List<MemberFollow> getFollowings(Member followerMember, Pageable pageable) {

        return memberFollowJpaRepository.findAllFollowingsByFollowerMemberFetchJoinMember(
                followerMember, pageable);
    }

    @Override
    public Optional<MemberFollow> getFollowing(String memberId, String followingMemberId) {
        return memberFollowJpaRepository.findByFollowerMemberIdAndFollowingMemberId(
                memberId, followingMemberId);
    }

    @Override
    public void delete(MemberFollow memberFollow) {
        memberFollowJpaRepository.delete(memberFollow);
    }
}
