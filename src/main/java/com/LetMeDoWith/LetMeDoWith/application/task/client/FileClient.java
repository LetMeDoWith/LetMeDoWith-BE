package com.LetMeDoWith.LetMeDoWith.application.task.client;

import java.time.Duration;

public interface FileClient {

    String getUploadPresignedUrl(String key, Duration expires);
}
