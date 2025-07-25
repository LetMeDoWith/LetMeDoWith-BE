package com.LetMeDoWith.LetMeDoWith.infrastructure.member.persistence.jpaRepository;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMemberSocialAccount;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QMemberJpaRepositoryImpl implements QMemberJpaRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private final QMember qMember = QMember.member;
    private final QMemberSocialAccount qMemberSocialAccount = QMemberSocialAccount.memberSocialAccount;

    @Override
    public Optional<Member> findByProviderAndSubject(SocialProvider provider, String subject) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(qMember)
                .leftJoin(qMemberSocialAccount)
                .on(qMember.eq(qMemberSocialAccount.member))
                .fetchJoin()
                .where(qMember.subject.eq(subject).and(qMemberSocialAccount.provider.eq(provider)))
                .fetchOne());
    }

    @Override
    public Optional<Member> findByProviderAndSubjectAndStatus(
            SocialProvider provider, String subject, MemberStatus status) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(qMember)
                .leftJoin(qMemberSocialAccount)
                .on(qMember.eq(qMemberSocialAccount.member))
                .fetchJoin()
                .where(qMember.subject
                        .eq(subject)
                        .and(qMemberSocialAccount.provider.eq(provider))
                        .and(qMember.status.eq(status)))
                .fetchOne());
    }
}
