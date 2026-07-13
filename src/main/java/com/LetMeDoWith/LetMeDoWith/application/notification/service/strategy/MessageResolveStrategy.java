package com.LetMeDoWith.LetMeDoWith.application.notification.service.strategy;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import java.util.List;
import java.util.Map;

public interface MessageResolveStrategy {
    boolean supports(NotificationTemplateCode templateCode);

    /**
     * receiverMemberIds와 bodyParams, deeplinkParams는 순서(index)로 매칭된다.
     * titleParams, bodyParams, deeplinkParams는 null 또는 빈 값이면 해당 파라미터 치환 없이 처리한다.
     */
    List<MessageContentVo> resolve(
            NotificationTemplateCode templateCode,
            List<String> receiverMemberIds,
            Map<String, String> titleParams,
            List<Map<String, String>> bodyParams,
            List<Map<String, String>> deeplinkParams);
}
