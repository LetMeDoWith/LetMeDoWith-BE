package com.LetMeDoWith.LetMeDoWith.application.notification.service.strategy;

import com.LetMeDoWith.LetMeDoWith.common.enums.notification.NotificationTemplateCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageResolveStrategyResolver {

    private final List<MessageResolveStrategy> strategies;

    public MessageResolveStrategy resolve(NotificationTemplateCode templateCode) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(templateCode))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No strategy found for template code: " + templateCode));
    }
}
