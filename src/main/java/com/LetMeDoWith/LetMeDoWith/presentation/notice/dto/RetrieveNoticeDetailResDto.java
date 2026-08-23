package com.LetMeDoWith.LetMeDoWith.presentation.notice.dto;

import com.LetMeDoWith.LetMeDoWith.application.notice.dto.RetrieveNoticeDetailResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "공지/이벤트 상세 조회 응답")
public record RetrieveNoticeDetailResDto(
        @Schema(description = "공지/이벤트 ID", example = "1") Long id,
        @Schema(description = "제목", example = "공지의 제목입니다") String title,
        @Schema(description = "내용", example = "공지의 내용입니다") String content,
        @Schema(description = "공지/이벤트 타입", example = "NOTICE") NoticeType type,
        @Schema(description = "생성일자", example = "2026-01-01T09:00:00") LocalDateTime createdAt,
        @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.png") String thumbnailImageUrl) {

    public static RetrieveNoticeDetailResDto of(
            Long id, String title, String content, NoticeType type, LocalDateTime createdAt, String thumbnailImageUrl) {
        return new RetrieveNoticeDetailResDto(id, title, content, type, createdAt, thumbnailImageUrl);
    }

    public static RetrieveNoticeDetailResDto from(RetrieveNoticeDetailResult result) {
        return new RetrieveNoticeDetailResDto(
                result.id(),
                result.title(),
                result.content(),
                result.type(),
                result.createdAt(),
                result.thumbnailImageUrl());
    }
}
