package com.LetMeDoWith.LetMeDoWith.application.notification.service.strategy;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import java.util.List;
import java.util.Map;

public interface MessageResolveStrategy {
    boolean supports(NotificationTemplateCode templateCode);

    /**
     * receiverMemberIds와 deeplinkParams는 순서(index)로 매칭된다.
     * deeplinkParams가 비어있으면 모든 수신자에 대해 deeplink 파라미터 없이 처리한다.
     */
    List<MessageContentVo> resolve(
            NotificationTemplateCode templateCode,
            List<String> receiverMemberIds,
            Map<String, String> titleParams,
            Map<String, String> bodyParams,
            List<Map<String, String>> deeplinkParams);
}
