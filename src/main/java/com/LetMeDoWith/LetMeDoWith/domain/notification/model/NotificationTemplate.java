package com.LetMeDoWith.LetMeDoWith.domain.notification.model;

import com.LetMeDoWith.LetMeDoWith.common.entity.BaseAuditEntity;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Column(name = "app_deep_link", nullable = true, columnDefinition = "TEXT")
    private String appDeepLink;

    public static NotificationTemplate of(String code, String title, String body, String deepLink) {
        return NotificationTemplate.builder()
                .code(code)
                .title(title)
                .body(body)
                .appDeepLink(deepLink)
                .build();
    }

    public String parseTitle(Map<String, String> params) {
        String titleTemplate = this.title;

        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(titleTemplate);

        Set<String> keySet = new HashSet<>();
        while (matcher.find()) {
            keySet.add(matcher.group(1));
        }

        if (!params.keySet().containsAll(keySet)) {
            throw new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR);
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            titleTemplate = titleTemplate.replace("{{" + key + "}}", value);
        }

        return titleTemplate;
    }

    public String parseBody(Map<String, String> params) {
        String bodyTemplate = this.body;

        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(bodyTemplate);

        Set<String> keySet = new HashSet<>();
        while (matcher.find()) {
            keySet.add(matcher.group(1));
        }

        if (!params.keySet().containsAll(keySet)) {
            throw new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR);
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            bodyTemplate = bodyTemplate.replace("{{" + key + "}}", value);
        }

        return bodyTemplate;
    }
}
