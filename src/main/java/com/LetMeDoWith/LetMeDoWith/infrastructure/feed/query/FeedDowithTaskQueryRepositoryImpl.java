package com.LetMeDoWith.LetMeDoWith.infrastructure.feed.query;

import com.LetMeDoWith.LetMeDoWith.application.feed.repository.FeedDowithTaskQueryRepository;
import com.LetMeDoWith.LetMeDoWith.application.feed.repository.dto.DowithTaskSuccessImageQueryDto;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.QMember;
import com.LetMeDoWith.LetMeDoWith.domain.task.enums.DowithTaskStatus;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTask;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTaskLike;
import com.LetMeDoWith.LetMeDoWith.domain.task.model.QDowithTaskSuccess;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FeedDowithTaskQueryRepositoryImpl implements FeedDowithTaskQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QDowithTask dowithTask = QDowithTask.dowithTask;
    private final QDowithTaskSuccess dowithTaskSuccess = QDowithTaskSuccess.dowithTaskSuccess;
    private final QDowithTaskLike dowithTaskLike = QDowithTaskLike.dowithTaskLike;
    private final QMember member = QMember.member;

    @Override
    public List<DowithTaskSuccessImageQueryDto> getDowithTaskSuccessImages(String requestMemberId, int offset, int limit) {
        return queryFactory
                .select(Projections.constructor(
                        DowithTaskSuccessImageQueryDto.class,
                        dowithTask.id,
                        dowithTask.title,
                        member.nickname,
                        member.profileImageUrl,
                        dowithTaskSuccess.imageUrl,
                        JPAExpressions
                                .selectOne()
                                .from(dowithTaskLike)
                                .where(dowithTaskLike.dowithTask.eq(dowithTask),
                                        dowithTaskLike.memberId.eq(requestMemberId))
                                .exists()

                ))
                .from(dowithTask)
                .fetchJoin()
                .leftJoin(member)
                .on(dowithTask.memberId.eq(member.id))
                .join(dowithTaskSuccess)
                .on(dowithTaskSuccess.dowithTask.eq(dowithTask))
                .where(dowithTask.status.eq(DowithTaskStatus.SUCCESS))
                .orderBy(dowithTaskSuccess.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public Map<Long, Long> countDowithTaskLikes(Set<Long> dowithTaskIds) {
        return queryFactory
                .select(dowithTask.id,
                        dowithTaskLike.count())
                .from(dowithTaskLike)
                .where(dowithTaskLike.dowithTask.id.in(dowithTaskIds))
                .groupBy(dowithTaskLike.dowithTask.id)
                .fetch()
                .stream()
                .collect(
                        Collectors.toMap(
                                tuple -> tuple.get(dowithTask.id),
                                tuple -> Optional.ofNullable(tuple.get(dowithTaskLike.count())).orElse(0L)
                        )
                );
    }
}
