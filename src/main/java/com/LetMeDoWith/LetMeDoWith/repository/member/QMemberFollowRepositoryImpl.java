package com.LetMeDoWith.LetMeDoWith.repository.member;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.LetMeDoWith.LetMeDoWith.entity.member.Member;
import com.LetMeDoWith.LetMeDoWith.entity.member.MemberFollow;
import com.LetMeDoWith.LetMeDoWith.entity.member.QMember;
import com.LetMeDoWith.LetMeDoWith.entity.member.QMemberFollow;
import com.LetMeDoWith.LetMeDoWith.enums.member.MemberStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QMemberFollowRepositoryImpl implements QMemberFollowRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private QMemberFollow qMemberFollow = QMemberFollow.memberFollow;
	private QMember qMember = QMember.member;

	@Override
	public List<MemberFollow> findAllFollowingsByFollowerMemberFetchJoinMember(Member followerMember, Pageable pageable) {
		return jpaQueryFactory.select(qMemberFollow)
			.from(qMemberFollow)
			.innerJoin(qMemberFollow.followingMember, qMember)
			.on(qMemberFollow.followingMember.status.eq(MemberStatus.NORMAL))
			.where(qMemberFollow.followerMember.eq(followerMember))
			.orderBy(qMemberFollow.createdAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	@Override
	public List<MemberFollow> findAllFollowersByFollowingMemberFetchJoinMember(Member followingMember, Pageable pageable) {
		return jpaQueryFactory.select(qMemberFollow)
			.from(qMemberFollow)
			.innerJoin(qMemberFollow.followerMember, qMember)
			.on(qMemberFollow.followerMember.status.eq(MemberStatus.NORMAL))
			.where(qMemberFollow.followingMember.eq(followingMember))
			.orderBy(qMemberFollow.createdAt.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}
