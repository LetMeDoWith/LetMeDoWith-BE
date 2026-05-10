package com.LetMeDoWith.LetMeDoWith.presentation.notice.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "공지/이벤트 생성 요청 (관리용)")
public record CreateNoticeReqDto(
        @Schema(description = "공지 유형", implementation = NoticeType.class, example = "NOTICE") NoticeType type,
        @Schema(description = "제목", example = "서비스 점검 안내") String title,
        @Schema(description = "본문 (HTML 등)", example = "<p>점검 시간: ...</p>") String content,
        @Schema(description = "노출 시작 일시") LocalDateTime startDateTime,
        @Schema(description = "노출 종료 일시") LocalDateTime endDateTime,
        @Schema(description = "목록/썸네일용 이미지 URL", example = "https://example.com/thumbnail.png") String thumbnailImageUrl
        // 공지 본문의 이미지는 어떻게 처리할지 논의 필요
        ) {}
