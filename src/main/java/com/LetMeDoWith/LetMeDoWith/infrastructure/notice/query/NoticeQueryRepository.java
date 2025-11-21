package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeQueryDto;
import java.util.List;
import java.util.Optional;

public interface NoticeQueryRepository {

    Long countNotices();

    List<NoticeQueryDto> getNotices(NoticeType type, long offset, int limit);

    Optional<NoticeDetailQueryDto> getNoticeDetail(Long noticeId);
}
