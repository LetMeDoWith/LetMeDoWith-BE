package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationTokenService {

    @Transactional
    public void registerToken(String userId, String token) {
        // TODO - 토큰을 DB에 저장
    }
}
