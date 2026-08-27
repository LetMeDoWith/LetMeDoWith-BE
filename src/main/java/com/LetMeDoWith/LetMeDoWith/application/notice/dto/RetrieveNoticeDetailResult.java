package com.LetMeDoWith.LetMeDoWith.application.notice.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeDetailQueryDto;
import java.time.LocalDateTime;

public record RetrieveNoticeDetailResult(
        Long id, String title, String content, NoticeType type, LocalDateTime createdAt, String thumbnailImageUrl) {

    public static RetrieveNoticeDetailResult of(
            Long id, String title, String content, NoticeType type, LocalDateTime createdAt, String thumbnailImageUrl) {
        return new RetrieveNoticeDetailResult(id, title, content, type, createdAt, thumbnailImageUrl);
    }

    public static RetrieveNoticeDetailResult from(NoticeDetailQueryDto dto) {
        return new RetrieveNoticeDetailResult(
                dto.id(), dto.title(), dto.content(), dto.type(), dto.createdAt(), dto.thumbnailImageUrl());
    }
}
