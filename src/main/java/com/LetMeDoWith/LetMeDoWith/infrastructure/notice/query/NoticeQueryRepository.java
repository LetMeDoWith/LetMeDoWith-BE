package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeQueryDto;
import java.util.List;

public interface NoticeQueryRepository {

    Long countNotices();

    List<NoticeQueryDto> getNotices(NoticeType type, long offset, int size);
}
