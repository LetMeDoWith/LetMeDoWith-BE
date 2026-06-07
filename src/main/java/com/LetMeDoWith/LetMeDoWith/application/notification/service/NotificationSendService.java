package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.application.notification.client.MessageServerClient;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationTemplate;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTemplateRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTokenRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private final MessageServerClient messageServerClient;

    private final NotificationRepository notificationRepository;
    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;

    private final MemberRepository memberRepository;

    @Async
    @Transactional
    public void sendNotification(
            String receiverMemberId,
            String templateCode,
            Map<String, String> titleParams,
            Map<String, String> bodyParams,
            NotificationType notificationType) {
        doSend(receiverMemberId, templateCode, titleParams, bodyParams, notificationType);
    }

    @Async
    @Transactional
    public void sendNotification(
            String senderMemberId, String receiverMemberId, String templateCode, NotificationType notificationType) {

        List<Member> members =
                memberRepository.getMembers(List.of(senderMemberId, receiverMemberId), MemberStatus.NORMAL);

        Map<String, Member> memberMap = members.stream().collect(Collectors.toMap(Member::getId, Function.identity()));

        Map<String, String> paramsMap = Map.of(
                "senderNickname", memberMap.get(senderMemberId).getNickname(),
                "receiverNickname", memberMap.get(receiverMemberId).getNickname());

        doSend(receiverMemberId, templateCode, paramsMap, paramsMap, notificationType);
    }

    private void doSend(
            String receiverMemberId,
            String templateCode,
            Map<String, String> titleParams,
            Map<String, String> bodyParams,
            NotificationType notificationType) {

        NotificationToken notificationToken = notificationTokenRepository
                .getNotificationToken(receiverMemberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        if (!notificationToken.isExpired()) throw new RestApiException(FailResponseStatus.INVALID_REQUEST);

        NotificationTemplate notificationTemplate = notificationTemplateRepository
                .getNotificationTemplate(templateCode)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        String title = notificationTemplate.parseTitle(titleParams);
        String body = notificationTemplate.parseBody(bodyParams);

        messageServerClient.sendMessage(
                notificationToken.getToken(),
                title,
                body,
                notificationTemplate.getImage(),
                notificationTemplate.getAppDeepLink(),
                () -> saveNotification(
                        receiverMemberId,
                        title,
                        body,
                        notificationTemplate.getAppDeepLink(),
                        templateCode,
                        notificationType),
                () -> expireToken(receiverMemberId));
    }

    private void saveNotification(
            String memberId,
            String title,
            String body,
            String deeplink,
            String notificationTemplateCode,
            NotificationType notificationType) {

        notificationRepository.save(
                Notification.of(memberId, title, body, deeplink, notificationTemplateCode, notificationType));
    }

    private void expireToken(String memberId) {

        NotificationToken notificationToken = notificationTokenRepository
                .getNotificationToken(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        notificationToken.expireToken();
        notificationTokenRepository.save(notificationToken);
    }
}
