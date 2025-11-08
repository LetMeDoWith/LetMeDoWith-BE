package com.LetMeDoWith.LetMeDoWith.application.notice.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import java.time.LocalDateTime;

public record RetrieveNoticeDetailResult(
        Long id, String title, String content, NoticeType type, LocalDateTime createdAt, String thumbnailImageUrl) {

    public static RetrieveNoticeDetailResult of(
            Long id, String title, String content, NoticeType type, LocalDateTime createdAt, String thumbnailImageUrl) {
        return new RetrieveNoticeDetailResult(id, title, content, type, createdAt, thumbnailImageUrl);
    }
}
