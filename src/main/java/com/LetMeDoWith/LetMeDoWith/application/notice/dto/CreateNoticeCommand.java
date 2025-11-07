package com.LetMeDoWith.LetMeDoWith.application.notice.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import java.time.LocalDateTime;

public record CreateNoticeCommand(
        NoticeType type,
        String title,
        String content,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String thumbnailImageUrl) {

    public static CreateNoticeCommand of(
            NoticeType type,
            String title,
            String content,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String thumbnailImageUrl) {
        return new CreateNoticeCommand(type, title, content, startDateTime, endDateTime, thumbnailImageUrl);
    }
}
