package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.domain.notice.model.QNotice;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeQueryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QNotice notice = QNotice.notice;

    @Override
    public Long countNotices() {
        return queryFactory.select(Wildcard.count).from(notice).fetchOne();
    }

    @Override
    public List<NoticeQueryDto> getNotices(NoticeType type, long offset, int size) {
        return queryFactory
                .select(Projections.constructor(
                        NoticeQueryDto.class,
                        notice.id,
                        notice.title,
                        notice.noticeType,
                        notice.createdAt,
                        notice.thumbnailImageUrl))
                .from(notice)
                .where(notice.deleteYn.eq(false))
                .offset(offset)
                .limit(size)
                .fetch();
    }
}
