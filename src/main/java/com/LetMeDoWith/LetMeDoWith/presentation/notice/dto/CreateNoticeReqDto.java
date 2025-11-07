package com.LetMeDoWith.LetMeDoWith.presentation.notice.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import java.time.LocalDateTime;

public record CreateNoticeReqDto(
        NoticeType type,
        String title,
        String content,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String thumbnailImageUrl
        // 공지 본문의 이미지는 어떻게 처리할지 논의 필요
        ) {}
