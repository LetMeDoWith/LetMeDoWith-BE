package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.domain.notice.model.QNotice;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QNotice notice = QNotice.notice;

    @Override
    public Long countNotices() {
        return queryFactory
                .select(Wildcard.count)
                .from(notice)
                .where(notice.isDeleted.eq(Yn.FALSE))
                .fetchOne();
    }

    @Override
    public List<NoticeQueryDto> getNotices(NoticeType type, long offset, int limit) {
        BooleanExpression eqNoticeType = type == null ? null : notice.noticeType.eq(type);

        return queryFactory
                .select(Projections.constructor(
                        NoticeQueryDto.class,
                        notice.id,
                        notice.title,
                        notice.noticeType,
                        notice.createdAt,
                        notice.thumbnailImageUrl))
                .from(notice)
                .where(notice.isDeleted.eq(Yn.FALSE).and(eqNoticeType))
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<NoticeDetailQueryDto> getNoticeDetail(Long noticeId) {
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(
                        NoticeDetailQueryDto.class,
                        notice.id,
                        notice.title,
                        notice.content,
                        notice.noticeType,
                        notice.createdAt,
                        notice.thumbnailImageUrl))
                .from(notice)
                .where(notice.id.eq(noticeId).and(notice.isDeleted.eq(Yn.FALSE)))
                .fetchOne());
    }
}
