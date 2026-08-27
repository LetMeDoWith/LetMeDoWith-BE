package com.LetMeDoWith.LetMeDoWith.application.auth.factory;

import com.LetMeDoWith.LetMeDoWith.application.auth.client.AuthClient;
import com.LetMeDoWith.LetMeDoWith.domain.auth.enums.SocialProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialProviderAuthFactory {

    private final ApplicationContext applicationContext;

    public AuthClient getClient(SocialProvider socialProvider) {

        return (AuthClient) applicationContext.getBean(socialProvider.getCode().toLowerCase() + "AuthClient");
    }
}
