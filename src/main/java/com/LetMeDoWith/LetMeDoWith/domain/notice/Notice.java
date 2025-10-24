package com.LetMeDoWith.LetMeDoWith.domain.notice;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.BaseEnum;
import com.LetMeDoWith.LetMeDoWith.infrastructure.notice.converter.NoticeTypeConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

    @Column(name = "main_text", nullable = false, columnDefinition = "TEXT")
    private String mainText;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "thumbnail_image_url", nullable = false)
    private String thumbnailImageUrl;

    @Getter
    @AllArgsConstructor
    @JsonDeserialize(using = NoticeTypeConverter.class)
    public enum NoticeType implements BaseEnum {
        NOTICE("NOTICE", "공지"),
        EVENT("EVENT", "이벤트"),
        ;

        private final String code;
        private final String description;
    }
}
