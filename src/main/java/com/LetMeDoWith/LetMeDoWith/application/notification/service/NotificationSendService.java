package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.application.notification.client.MessageServerClient;
import com.LetMeDoWith.LetMeDoWith.application.notification.dto.SendNotificationResult;
import com.LetMeDoWith.LetMeDoWith.application.notification.dto.messageServer.PushMessageDto;
import com.LetMeDoWith.LetMeDoWith.application.notification.dto.messageServer.SendMessageResult;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.strategy.MessageContentVo;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.strategy.MessageResolveStrategy;
import com.LetMeDoWith.LetMeDoWith.application.notification.service.strategy.MessageResolveStrategyResolver;
import com.LetMeDoWith.LetMeDoWith.common.enums.member.MemberStatus;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationType;
import com.LetMeDoWith.LetMeDoWith.common.exception.RestApiException;
import com.LetMeDoWith.LetMeDoWith.common.exception.status.FailResponseStatus;
import com.LetMeDoWith.LetMeDoWith.domain.member.model.Member;
import com.LetMeDoWith.LetMeDoWith.domain.member.repository.MemberRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.Notification;
import com.LetMeDoWith.LetMeDoWith.domain.notification.model.NotificationToken;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationRepository;
import com.LetMeDoWith.LetMeDoWith.domain.notification.repository.NotificationTokenRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSendService {

    private static final String RECEIVER_MEMBER_ID_META_KEY = "receiverMemberId";

    private final MessageServerClient messageServerClient;

    private final MessageResolveStrategyResolver messageResolveStrategyResolver;

    private final NotificationRepository notificationRepository;
    private final NotificationTokenRepository notificationTokenRepository;

    private final MemberRepository memberRepository;

    @Async
    @Transactional
    public void sendNotificationAsync(
            NotificationTemplateCode templateCode,
            String receiverMemberId,
            Map<String, String> titleParam,
            Map<String, String> bodyParam) {
        doSend(templateCode, receiverMemberId, titleParam, bodyParam);
    }

    @Async
    @Transactional
    public void sendNotificationAsync(
            NotificationTemplateCode templateCode, String senderMemberId, String receiverMemberId) {

        List<Member> members =
                memberRepository.getMembers(List.of(senderMemberId, receiverMemberId), MemberStatus.NORMAL);

        Map<String, Member> memberMap = members.stream().collect(Collectors.toMap(Member::getId, Function.identity()));

        Map<String, String> paramsMap = Map.of(
                "senderNickname", memberMap.get(senderMemberId).getNickname(),
                "receiverNickname", memberMap.get(receiverMemberId).getNickname());

        doSend(templateCode, receiverMemberId, paramsMap, paramsMap);
    }

    @Transactional
    public SendNotificationResult sendNotifications(
            NotificationTemplateCode templateCode,
            List<String> receiverMemberIds,
            Map<String, String> titleParams,
            Map<String, String> bodyParams,
            List<Map<String, String>> deeplinkParams) {
        return doSends(templateCode, receiverMemberIds, titleParams, bodyParams, deeplinkParams);
    }

    private void doSend(
            NotificationTemplateCode templateCode,
            String receiverMemberId,
            Map<String, String> titleParams,
            Map<String, String> bodyParams) {

        MessageResolveStrategy strategy = messageResolveStrategyResolver.resolve(templateCode);
        List<MessageContentVo> messageContents =
                strategy.resolve(templateCode, List.of(receiverMemberId), titleParams, bodyParams, List.of(Map.of()));

        if (messageContents.isEmpty()) {
            throw new RestApiException(FailResponseStatus.INVALID_REQUEST);
        }
        MessageContentVo messageContent = messageContents.get(0);

        messageServerClient.sendMessage(
                messageContent.notificationToken(),
                messageContent.title(),
                messageContent.body(),
                messageContent.imageUrl(),
                messageContent.deeplink(),
                () -> saveNotification(
                        messageContent.receiverMemberId(),
                        messageContent.title(),
                        messageContent.body(),
                        messageContent.imageUrl(),
                        messageContent.deeplink(),
                        templateCode,
                        messageContent.type()),
                () -> expireToken(messageContent.receiverMemberId()));
    }

    private SendNotificationResult doSends(
            NotificationTemplateCode templateCode,
            List<String> receiverMemberIds,
            Map<String, String> titleParams,
            Map<String, String> bodyParams,
            List<Map<String, String>> deeplinkParams) {

        if (receiverMemberIds.isEmpty()) {
            return new SendNotificationResult(Set.of(), Set.of());
        }

        MessageResolveStrategy strategy = messageResolveStrategyResolver.resolve(templateCode);
        List<MessageContentVo> messageContents =
                strategy.resolve(templateCode, receiverMemberIds, titleParams, bodyParams, deeplinkParams);

        Set<String> requestedMemberIds = new HashSet<>(receiverMemberIds);
        Set<String> resolvedMemberIds =
                messageContents.stream().map(MessageContentVo::receiverMemberId).collect(Collectors.toSet());
        Set<String> unresolvedMemberIds = new HashSet<>(requestedMemberIds);
        unresolvedMemberIds.removeAll(resolvedMemberIds);

        if (messageContents.isEmpty()) {
            return new SendNotificationResult(Set.of(), unresolvedMemberIds);
        }

        List<PushMessageDto> pushMessages = messageContents.stream()
                .map(messageContent -> new PushMessageDto(
                        messageContent.notificationToken(),
                        messageContent.title(),
                        messageContent.body(),
                        messageContent.imageUrl(),
                        messageContent.deeplink(),
                        Map.of(RECEIVER_MEMBER_ID_META_KEY, messageContent.receiverMemberId())))
                .toList();

        SendMessageResult sendMessageResult = messageServerClient.sendMessages(pushMessages);

        Set<String> sendFailedMemberIds = sendMessageResult.failMessages().stream()
                .map(failMessage -> (String) failMessage.metaData().get(RECEIVER_MEMBER_ID_META_KEY))
                .collect(Collectors.toSet());

        for (MessageContentVo messageContent : messageContents) {
            if (sendFailedMemberIds.contains(messageContent.receiverMemberId())) continue;

            saveNotification(
                    messageContent.receiverMemberId(),
                    messageContent.title(),
                    messageContent.body(),
                    messageContent.imageUrl(),
                    messageContent.deeplink(),
                    templateCode,
                    messageContent.type());
        }

        // TODO - 만료된 토큰(UNREGISTERED)으로 인한 실패 건 NotificationToken expire 처리 필요

        Set<String> failedMemberIds = new HashSet<>(unresolvedMemberIds);
        failedMemberIds.addAll(sendFailedMemberIds);

        Set<String> succeededMemberIds = new HashSet<>(resolvedMemberIds);
        succeededMemberIds.removeAll(sendFailedMemberIds);

        return new SendNotificationResult(succeededMemberIds, failedMemberIds);
    }

    private void saveNotification(
            String memberId,
            String title,
            String body,
            String imageUrl,
            String deeplink,
            NotificationTemplateCode notificationTemplateCode,
            NotificationType notificationType) {

        notificationRepository.save(Notification.of(
                memberId, title, body, imageUrl, deeplink, notificationTemplateCode.getCode(), notificationType));
    }

    private void expireToken(String memberId) {

        NotificationToken notificationToken = notificationTokenRepository
                .getNotificationToken(memberId)
                .orElseThrow(() -> new RestApiException(FailResponseStatus.INVALID_REQUEST));
        notificationToken.expireToken();
        notificationTokenRepository.save(notificationToken);
    }
}
