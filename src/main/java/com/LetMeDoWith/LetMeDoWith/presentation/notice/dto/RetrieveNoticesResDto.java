package com.LetMeDoWith.LetMeDoWith.presentation.notice.dto;

import com.LetMeDoWith.LetMeDoWith.application.notice.dto.RetrieveNoticesResult;
import com.LetMeDoWith.LetMeDoWith.application.notice.dto.RetrieveNoticesResult.RetrieveNoticeResult;
import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "공지사항/이벤트 목록 조회 응답")
public record RetrieveNoticesResDto(
    @Schema(description = "공지사항/이벤트 목록")
    List<RetrieveNoticeResDto> notices
) {

    public static RetrieveNoticesResDto from(RetrieveNoticesResult result) {
        return new RetrieveNoticesResDto(
            result.notices().stream().map(RetrieveNoticeResDto::from).toList());
    }

    public record RetrieveNoticeResDto(
        @Schema(description = "공지/이벤트 ID", example = "1")
        Long id,
        @Schema(description = "제목", example = "공지의 제목입니다")
        String title,
        @Schema(description = "공지/이벤트 타입", example = "\"NOTICE\" | \"EVENT\"")
        NoticeType type,
        @Schema(description = "생성일자")
        LocalDateTime createdAt,
        @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.png")
        String thumbnailImageUrl

    ) {

        public static RetrieveNoticeResDto from(RetrieveNoticeResult result) {
            return new RetrieveNoticeResDto(result.id(), result.title(), result.type(),
                result.createdAt(),
                result.thumbnailImageUrl());
        }

    }
}