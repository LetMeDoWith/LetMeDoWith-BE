package com.LetMeDoWith.LetMeDoWith.application.notice.dto;

import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.query.dto.NoticeQueryDto;
import java.time.LocalDateTime;
import java.util.List;

public record RetrieveNoticesResult(Long totalCount, List<RetrieveNoticeResult> notices) {

    public static RetrieveNoticesResult from(Long totalCount, List<NoticeQueryDto> noticeQueryDtos) {
        return new RetrieveNoticesResult(
                totalCount,
                noticeQueryDtos.stream().map(RetrieveNoticeResult::from).toList());
    }

    public record RetrieveNoticeResult(
            Long id, String title, NoticeType type, LocalDateTime createdAt, String thumbnailImageUrl) {

        public static RetrieveNoticeResult from(NoticeQueryDto queryDto) {
            return new RetrieveNoticeResult(
                    queryDto.id(),
                    queryDto.title(),
                    queryDto.type(),
                    queryDto.createdAt(),
                    queryDto.thumbnailImageUrl());
        }
    }
}
