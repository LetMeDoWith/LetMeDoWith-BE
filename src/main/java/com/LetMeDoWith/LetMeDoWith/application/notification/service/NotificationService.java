package com.LetMeDoWith.LetMeDoWith.application.notification.service;

import com.LetMeDoWith.LetMeDoWith.application.notification.client.MessageServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MessageServerClient messageServerClient;

    @Transactional
    public void sendNotification(String memberId, String templateCode, Map<String, String> titleParams, Map<String, String> bodyParams) {

    }
}
