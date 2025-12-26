package com.LetMeDoWith.LetMeDoWith.domain.task.client;

import java.time.Duration;

public interface FileClient {

    String getUploadPresignedUrl(String key, Duration expires);
}
