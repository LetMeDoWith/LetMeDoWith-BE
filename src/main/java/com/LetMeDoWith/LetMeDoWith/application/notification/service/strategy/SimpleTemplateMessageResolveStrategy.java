package com.LetMeDoWith.LetMeDoWith.application.notification.service.strategy;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTemplateRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleTemplateMessageResolveStrategy implements MessageResolveStrategy {

    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationTokenRepository notificationTokenRepository;

    @Override
    public boolean supports(NotificationTemplateCode templateCode) {
        return !RandomTemplateMessageResolveStrategy.RANDOM_NOTIFICATION_TEMPLATE_CODES.contains(templateCode);
    }

    @Override
    public List<MessageContentVo> resolve(
            NotificationTemplateCode templateCode,
            List<String> receiverMemberIds,
            Map<String, String> titleParams,
            Map<String, String> bodyParams,
            List<Map<String, String>> deeplinkParams) {

        Set<String> uniqueMemberIds = new HashSet<>(receiverMemberIds);
        Map<String, NotificationToken> notificationTokenMap =
                notificationTokenRepository.getActiveNotificationTokens(uniqueMemberIds).stream()
                        .collect(Collectors.toMap(NotificationToken::getMemberId, token -> token));

        if (notificationTokenMap.size() != uniqueMemberIds.size()) {
            Set<String> missingTokenMemberIds = new HashSet<>(uniqueMemberIds);
            missingTokenMemberIds.removeAll(notificationTokenMap.keySet());
            log.error("{} 회원의 알림 토큰이 존재하지 않습니다. {} 알림 전송에 실패했습니다.", missingTokenMemberIds, templateCode.getCode());
        }

        NotificationTemplate notificationTemplate = notificationTemplateRepository
                .getNotificationTemplate(templateCode)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INTERNAL_SERVER_ERROR));
        String parsedTitle = notificationTemplate.parseTitle(titleParams);
        String parsedBody = notificationTemplate.parseBody(bodyParams);
        String imageUrl = notificationTemplate.getImageUrl();

        List<MessageContentVo> messageContents = new ArrayList<>();
        for (int i = 0; i < receiverMemberIds.size(); i++) {
            String memberId = receiverMemberIds.get(i);
            NotificationToken notificationToken = notificationTokenMap.get(memberId);
            if (notificationToken == null) continue;

            Map<String, String> deeplinkParam = deeplinkParams.isEmpty() ? Map.of() : deeplinkParams.get(i);
            messageContents.add(new MessageContentVo(
                    memberId,
                    notificationToken.getToken(),
                    parsedTitle,
                    parsedBody,
                    notificationTemplate.parseDeepLink(deeplinkParam),
                    imageUrl,
                    notificationTemplate.getType()));
        }
        return messageContents;
    }
}
