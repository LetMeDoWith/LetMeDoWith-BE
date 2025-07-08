package com.LetMeDoWith.LetMeDoWith.client;

import com.LetMeDoWith.LetMeDoWith.infrastructure.notification.client.FirebaseCloudMessageClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FirebaseCloudMessageClientTest {

    private final String fcmToken =
            "fx5STrP_eh7XIRNiVvNBk_:APA91bHpJ_SvZQTs8SK-Hkl5d8vChDEb2_njBRp-uLtzWU-3_s5W9aoL6OprShJG-ZIU4oSSDD4cfvB0jKb8xUcjvLWyVvhDkiM9DhsdrxhKa0wwrDwx-YI";

    @Autowired
    FirebaseCloudMessageClient firebaseCloudMessageClient;

    @Test
    void sendMessage() {
        // Given
        String title = "Test Title2";
        String body = "Test Body";

        // When
        firebaseCloudMessageClient.sendMessage(fcmToken, title, body, "letmedowith://test");

        // Then
        // Assertions can be added here to verify the behavior of sendMessage method
        // For example, you can verify that firebaseMessaging.send() was called with the correct
        // parameters
    }
}
