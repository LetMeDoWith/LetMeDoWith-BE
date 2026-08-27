package com.LetMeDoWith.LetMeDoWith.application.notification.dto.messageServer;

import java.util.List;
import java.util.Map;

public record SendMessageResult(int successCount, int failCount, List<FailMessage> failMessages) {
    public record FailMessage(String messageId, Map<String, Object> metaData, Exception exception) {}
}
