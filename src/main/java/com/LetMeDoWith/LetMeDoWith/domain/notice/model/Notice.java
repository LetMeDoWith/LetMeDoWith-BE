package com.LetMeDoWith.LetMeDoWith.domain.notice.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.common.Yn;
import com.LetMeDoWith.LetMeDoWith.common.enums.notice.NoticeType;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.common.util.SystemTimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "notice")
public class Notice extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "notice_type", nullable = false)
    private NoticeType noticeType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "delete_yn", nullable = false, columnDefinition = "VARCHAR(1)")
    private Yn isDeleted;

    @Column(name = "thumbnail_image_url", nullable = false)
    private String thumbnailImageUrl;

    public static Notice of(
            NoticeType type,
            String title,
            String content,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String thumbnailImageUrl) {
        Notice notice = Notice.builder()
                .noticeType(type)
                .title(title)
                .content(content)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .isDeleted(Yn.FALSE)
                .thumbnailImageUrl(thumbnailImageUrl)
                .build();

        // 추후에 HTML content로 간다면 XSS Sanitize 필요

        notice.validate();

        return notice;
    }

    private void validate() {
        LocalDateTime nowDateTime = SystemTimeUtil.now();

        if (endDateTime.isBefore(startDateTime)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        if (startDateTime.isBefore(nowDateTime) || endDateTime.isBefore(nowDateTime)) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }

        if (content.isBlank() || title.isBlank()) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
    }
}
