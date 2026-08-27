package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import java.time.LocalDateTime;

public record NoticeQueryDto(
        Long id, String title, NoticeType type, LocalDateTime createdAt, String thumbnailImageUrl) {}
