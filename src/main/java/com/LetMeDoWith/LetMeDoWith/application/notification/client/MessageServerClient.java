package com.LetMeDoWith.LetMeDoWith.application.notification.client;

import com.LetMeDoWith.LetMeDoWith.application.notification.dto.messageServer.PushMessageDto;
import com.LetMeDoWith.LetMeDoWith.application.notification.dto.messageServer.SendMessageResult;
import java.util.List;

public interface MessageServerClient {
    void sendMessage(
            String token,
            String title,
            String body,
            String image,
            String link,
            Runnable onSuccess,
            Runnable onFailureByExpiredToken);

    SendMessageResult sendMessages(List<PushMessageDto> messages);
}
