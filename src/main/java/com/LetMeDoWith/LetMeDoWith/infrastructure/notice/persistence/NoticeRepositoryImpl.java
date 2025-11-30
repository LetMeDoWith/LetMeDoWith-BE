package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.persistence;

import com.LetMeDoWith.LetMeDoWith.domain.notice.model.Notice;
import com.LetMeDoWith.LetMeDoWith.domain.notice.repository.NoticeRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.persistence.jpaRepository.NoticeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public Notice save(Notice notice) {
        return noticeJpaRepository.save(notice);
    }
}
