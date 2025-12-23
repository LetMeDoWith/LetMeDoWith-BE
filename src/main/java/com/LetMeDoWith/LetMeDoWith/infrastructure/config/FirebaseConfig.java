package com.LetMeDoWith.LetMeDoWith.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config-base64:#{null}}")
    private String firebaseConfigBase64;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount;

        if (firebaseConfigBase64 != null && !firebaseConfigBase64.isBlank()) {
            // 환경변수(Base64)가 있으면 우선 사용 (배포 환경)
            byte[] decodedConfig = Base64.getDecoder().decode(firebaseConfigBase64);
            serviceAccount = new ByteArrayInputStream(decodedConfig);
        } else {
            // 없으면 기존 로컬 파일 사용 (로컬 개발 환경)
            serviceAccount = new ClassPathResource("firebase-confidential.json").getInputStream();
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        // FirebaseApp 중복 초기화 방지
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
