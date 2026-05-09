package com.LetMeDoWith.LetMeDoWith.infrastructure.member.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberQueryRepository;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.dto.MemberDowithQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QMember qMember = QMember.member;
    private final QDowithTask qDowithTask = QDowithTask.dowithTask;

    @Override
    public Optional<MemberDowithQueryDto> getMyDowithInfo(String memberId) {
        MemberDowithQueryDto result = queryFactory
                .select(Projections.constructor(
                        MemberDowithQueryDto.class,
                        qMember.id,
                        qMember.nickname,
                        qMember.selfDescription,
                        qMember.profileImageUrl,
                        JPAExpressions.select(qDowithTask.id.count())
                                .from(qDowithTask)
                                .where(qDowithTask
                                        .memberId
                                        .eq(qMember.id)
                                        .and(qDowithTask.status.eq(DowithTaskStatus.SUCCESS)))))
                .from(qMember)
                .where(qMember.id.eq(memberId).and(qMember.status.eq(MemberStatus.NORMAL)))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
