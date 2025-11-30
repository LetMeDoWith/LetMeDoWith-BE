package com.LetMeDoWith.LetMeDoWith.domain.notice.repository;

import com.LetMeDoWith.LetMeDoWith.domain.notice.model.Notice;

public interface NoticeRepository {

    Notice save(Notice notice);
}
