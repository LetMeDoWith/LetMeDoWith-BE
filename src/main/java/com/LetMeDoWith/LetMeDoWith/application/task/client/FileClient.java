package com.LetMeDoWith.LetMeDoWith.application.task.client;

import java.time.Duration;

public interface FileClient {

    String getPresignedUrl(String bucketName, String fileName, Duration expires);

}
