package com.LetMeDoWith.LetMeDoWith.domain.notification.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.*;

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

    @Column(name = "code", nullable = false)
    private NotificationTemplateCode code;

    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "image_url", nullable = true, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "app_deep_link", nullable = false, columnDefinition = "TEXT")
    private String appDeepLink;

    public static NotificationTemplate of(
            NotificationTemplateCode code, NotificationType type, String title, String body, String deepLink) {
        return NotificationTemplate.builder()
                .code(code)
                .type(type)
                .title(title)
                .body(body)
                .appDeepLink(deepLink)
                .build();
    }

    public String parseTitle(Map<String, String> params) {
        return parse("title", this.title, params);
    }

    public String parseBody(Map<String, String> params) {
        return parse("body", this.body, params);
    }

    public String parseDeepLink(Map<String, String> params) {
        return parse("appDeepLink", this.appDeepLink, params);
    }

    private String parse(String fieldName, String template, Map<String, String> params) {

        if (params == null) return template;

        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(template);

        Set<String> keySet = new HashSet<>();
        while (matcher.find()) {
            keySet.add(matcher.group(1));
        }

        if (!params.keySet().containsAll(keySet)) {
            Set<String> missingKeys = new HashSet<>(keySet);
            missingKeys.removeAll(params.keySet());

            throw new IllegalStateException(
                    "알림 템플릿 파싱 실패 - templateCode: %s, field: %s, 필요한 placeholder: %s, 누락된 key: %s, 전달받은 paramKeys: %s"
                            .formatted(this.code, fieldName, keySet, missingKeys, params.keySet()));
        }

        String parsed = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            parsed = parsed.replace("{{" + key + "}}", value);
        }

        return parsed;
    }
}
