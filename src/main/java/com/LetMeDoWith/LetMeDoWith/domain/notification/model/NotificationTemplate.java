package com.LetMeDoWith.LetMeDoWith.domain.notification.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Table(name = "notification_template")
public class NotificationTemplate extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    public static NotificationTemplate of(String code, String title, String body) {
        return NotificationTemplate.builder()
                .code(code)
                .title(title)
                .body(body)
                .build();
    }

    public String parseTitle(Map<String, String> params) {

        String titleTemplate = this.title;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            titleTemplate = titleTemplate.replace("{{" + key + "}}", value);
        }

        return titleTemplate;
    }

    public String parseBody(Map<String, String> params) {
        String bodyTemplate = this.body;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            bodyTemplate = bodyTemplate.replace("{{" + key + "}}", value);
        }

        return bodyTemplate;
    }

}
