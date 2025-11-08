package com.LetMeDoWith.LetMeDoWith.application.notice.service;

import com.LetMeDoWith.LetMeDoWith.application.notice.dto.RetrieveNoticeDetailResult;
import com.LetMeDoWith.LetMeDoWith.application.notice.dto.RetrieveNoticesResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.NoticeQueryRepository;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeDetailQueryDto;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeQueryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetrieveNoticeService {

    private final NoticeQueryRepository noticeQueryRepository;

    public RetrieveNoticesResult retrieveNotices(NoticeType type, Pageable pageable) {
        Long totalCount = noticeQueryRepository.countNotices();
        List<NoticeQueryDto> notices =
                noticeQueryRepository.getNotices(type, pageable.getOffset(), pageable.getPageSize());

        return RetrieveNoticesResult.from(totalCount, notices);
    }

    public RetrieveNoticeDetailResult retrieveNoticeDetail(Long noticeId) {
        NoticeDetailQueryDto noticeDetail = noticeQueryRepository
                .getNoticeDetail(noticeId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.NOT_FOUND));

        return RetrieveNoticeDetailResult.of(
                noticeDetail.id(),
                noticeDetail.title(),
                noticeDetail.content(),
                noticeDetail.type(),
                noticeDetail.createdAt(),
                noticeDetail.thumbnailImageUrl());
    }
}
