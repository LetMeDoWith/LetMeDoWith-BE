package com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.LetMeDoWith.LetMeDoWith.domain.notice.model.Notice}
 */
public record NoticeDetailQueryDto(
        Long id, String title, String content, NoticeType type, LocalDateTime createdAt, String thumbnailImageUrl) {}
